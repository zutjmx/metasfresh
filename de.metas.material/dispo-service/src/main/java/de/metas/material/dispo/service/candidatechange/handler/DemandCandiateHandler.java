package de.metas.material.dispo.service.candidatechange.handler;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import de.metas.material.dispo.CandidateRepository;
import de.metas.material.dispo.CandidateSpecification.Type;
import de.metas.material.dispo.candidate.Candidate;
import de.metas.material.dispo.service.candidatechange.StockCandidateService;
import de.metas.material.event.MaterialDemandEvent;
import de.metas.material.event.MaterialEventService;
import lombok.NonNull;

/*
 * #%L
 * metasfresh-material-dispo-service
 * %%
 * Copyright (C) 2017 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@Service
public class DemandCandiateHandler implements CandidateHandler
{
	@NonNull
	private final CandidateRepository candidateRepository;

	@NonNull
	private final MaterialEventService materialEventService;

	@NonNull
	private final StockCandidateService stockCandidateService;

	public DemandCandiateHandler(
			@NonNull final CandidateRepository candidateRepository,
			@NonNull final MaterialEventService materialEventService,
			@NonNull final StockCandidateService stockCandidateService)
	{
		this.candidateRepository = candidateRepository;
		this.materialEventService = materialEventService;
		this.stockCandidateService = stockCandidateService;
	}

	@Override
	public Collection<Type> getHandeledTypes()
	{
		return ImmutableList.of(Type.DEMAND, Type.UNRELATED_DECREASE);
	}

	/**
	 * Persists (updates or creates) the given demand candidate and also it's <b>child</b> stock candidate.
	 * 
	 * @param demandCandidate
	 * @return
	 */
	public Candidate onCandidateNewOrChange(@NonNull final Candidate demandCandidate)
	{
		assertTCorrectCandidateType(demandCandidate);

		final Candidate demandCandidateWithId = candidateRepository.addOrUpdateOverwriteStoredSeqNo(demandCandidate);

		if (demandCandidateWithId.getQuantity().signum() == 0)
		{
			// this candidate didn't change anything
			return demandCandidateWithId;
		}

		// this is the seqno which the new stock candidate shall get according to the demand candidate
		final int expectedStockSeqNo = demandCandidateWithId.getSeqNo() + 1;

		final Candidate childStockWithDemand;

		final Optional<Candidate> possibleChildStockCandidate = candidateRepository.retrieveSingleChild(demandCandidateWithId.getId());
		if (possibleChildStockCandidate.isPresent())
		{
			// this supply candidate is not new and already has a stock candidate as its parent. be sure to update exactly *that* scandidate
			childStockWithDemand = stockCandidateService.updateStock(
					demandCandidateWithId, () -> {
						// don't check if we might create a new stock candidate, because we know we don't.
						// Instead we might run into trouble with CandidateRepository.retrieveExact() and multiple matching records.
						// So get the one that we know already exists and just update its quantity
						final Candidate childStockCandidate = possibleChildStockCandidate.get();
						return candidateRepository.updateQty(
								childStockCandidate
										.withQuantity(
												childStockCandidate.getQuantity().subtract(demandCandidateWithId.getQuantity())));
					});
		}

		else
		{
			childStockWithDemand = stockCandidateService.addOrUpdateStock(
					demandCandidate
							.withSeqNo(expectedStockSeqNo)
							.withQuantity(demandCandidateWithId.getQuantity().negate())
							.withParentId(demandCandidateWithId.getId()));
		}

		final Candidate demandCandidateToReturn;

		if (childStockWithDemand.getSeqNo() != expectedStockSeqNo)
		{
			// there was already a stock candidate which already had a seqNo.
			// keep it and in turn update the demandCandidate's seqNo accordingly
			demandCandidateToReturn = demandCandidate
					.withSeqNo(childStockWithDemand.getSeqNo() - 1);
			candidateRepository.addOrUpdateOverwriteStoredSeqNo(demandCandidateToReturn);
		}
		else
		{
			demandCandidateToReturn = demandCandidateWithId;
		}

		final boolean demandExceedsAvailableQty = childStockWithDemand.getQuantity().signum() < 0;
		if (demandExceedsAvailableQty && demandCandidate.getType() == Type.DEMAND)
		{
			// there would be no more stock left, so
			// notify whoever is in charge that we have a demand to balance
			final BigDecimal requiredAdditionalQty = childStockWithDemand.getQuantity().negate();

			final MaterialDemandEvent materialDemandEvent = MaterialDemandEventCreator.createMaterialDemandEvent(demandCandidateWithId, requiredAdditionalQty);
			materialEventService.fireEvent(materialDemandEvent);
		}
		return demandCandidateToReturn;
	}

	private void assertTCorrectCandidateType(@NonNull final Candidate demandCandidate)
	{
		final Type type = demandCandidate.getType();

		Preconditions.checkArgument(
				type == Type.DEMAND || type == Type.UNRELATED_DECREASE,
				"Given parameter 'demandCandidate' has type=%s; demandCandidate=%s",
				type, demandCandidate);
	}
}
