package de.metas.manufacturing.dispo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.adempiere.ad.dao.IQueryBL;
import org.adempiere.ad.dao.impl.CompareQueryFilter.Operator;
import org.adempiere.model.InterfaceWrapperHelper;
import org.adempiere.util.Services;
import org.springframework.stereotype.Service;

import de.metas.manufacturing.dispo.Candidate.Type;
import de.metas.manufacturing.dispo.model.I_MD_Candidate;
import de.metas.manufacturing.dispo.model.X_MD_Candidate;
import de.metas.quantity.Quantity;
import lombok.NonNull;

/*
 * #%L
 * metasfresh-manufacturing-dispo
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
public class CandidateRepository
{
	public void add(@NonNull final Candidate candidate)
	{
		final I_MD_Candidate candidadteRecord = retrieveExact(candidate);
		syncToRecord(candidadteRecord, candidate);
		InterfaceWrapperHelper.save(candidadteRecord);
	}

	private I_MD_Candidate retrieveExact(@NonNull final Candidate candidate)
	{
		final I_MD_Candidate candidateRecord = Services.get(IQueryBL.class)
				.createQueryBuilder(I_MD_Candidate.class)
				.addOnlyActiveRecordsFilter()
				.addEqualsFilter(I_MD_Candidate.COLUMN_MD_Candidate_Type, candidate.getType().toString())
				.addEqualsFilter(I_MD_Candidate.COLUMN_M_Locator_ID, candidate.getLocator().getM_Locator_ID())
				.addEqualsFilter(I_MD_Candidate.COLUMN_M_Product_ID, candidate.getProduct().getM_Product_ID())
				.addEqualsFilter(I_MD_Candidate.COLUMN_DateProjected, candidate.getDate())
				.create()
				.firstOnly(I_MD_Candidate.class); // note that we have a UC to make sure there is just one

		if (candidateRecord == null)
		{
			return InterfaceWrapperHelper.newInstance(I_MD_Candidate.class);
		}
		return candidateRecord;
	}

	private void syncToRecord(final I_MD_Candidate candidateRecord, final Candidate candidate)
	{
		candidateRecord.setMD_Candidate_Type(candidate.getType().toString());
		candidateRecord.setM_Locator(candidate.getLocator());
		candidateRecord.setM_Product(candidate.getProduct());
		candidateRecord.setC_UOM(candidate.getQuantity().getUOM());
		candidateRecord.setQty(candidate.getQuantity().getQty());
		candidateRecord.setDateProjected(new Timestamp(candidate.getDate().getTime()));
	}

	private Optional<Candidate> fromCandidateRecord(final I_MD_Candidate candidateRecord)
	{
		if (candidateRecord == null)
		{
			return Optional.empty();
		}
		return Optional
				.of(Candidate.builder()
						.type(Type.valueOf(candidateRecord.getMD_Candidate_Type()))
						.locator(candidateRecord.getM_Locator())
						.product(candidateRecord.getM_Product())
						.date(candidateRecord.getDateProjected())
						.quantity(new Quantity(candidateRecord.getQty(), candidateRecord.getC_UOM()))
						.build());
	}

	/**
	 * Returns the "oldest" stock candidate that is not after the given query's date.
	 *
	 * @param query
	 * @return
	 */
	public Optional<Candidate> retrieveStockAt(@NonNull final CandidatesSegment query)
	{
		final I_MD_Candidate candidateRecord = Services.get(IQueryBL.class).createQueryBuilder(I_MD_Candidate.class)
				.addOnlyActiveRecordsFilter()
				.addEqualsFilter(I_MD_Candidate.COLUMN_MD_Candidate_Type, X_MD_Candidate.MD_CANDIDATE_TYPE_STOCK)
				.addEqualsFilter(I_MD_Candidate.COLUMN_M_Locator_ID, query.getLocator().getM_Locator_ID())
				.addEqualsFilter(I_MD_Candidate.COLUMN_M_Product_ID, query.getProduct().getM_Product_ID())
				.addCompareFilter(I_MD_Candidate.COLUMN_DateProjected, Operator.LESS_OR_EQUAL, query.getProjectedDate())
				.orderBy().addColumn(I_MD_Candidate.COLUMNNAME_DateProjected, false).endOrderBy()
				.create()
				.first();

		return fromCandidateRecord(candidateRecord);
	}

	public List<Candidate> retrieveStockFrom(@NonNull final CandidatesSegment query)
	{
		final Stream<I_MD_Candidate> candidateRecords = Services.get(IQueryBL.class).createQueryBuilder(I_MD_Candidate.class)
				.addOnlyActiveRecordsFilter()
				.addEqualsFilter(I_MD_Candidate.COLUMN_MD_Candidate_Type, X_MD_Candidate.MD_CANDIDATE_TYPE_STOCK)
				.addEqualsFilter(I_MD_Candidate.COLUMN_M_Locator_ID, query.getLocator().getM_Locator_ID())
				.addEqualsFilter(I_MD_Candidate.COLUMN_M_Product_ID, query.getProduct().getM_Product_ID())
				.addCompareFilter(I_MD_Candidate.COLUMN_DateProjected, Operator.GREATER_OR_EQUAL, query.getProjectedDate())
				.orderBy().addColumn(I_MD_Candidate.COLUMNNAME_DateProjected, true).endOrderBy()
				.create()
				.stream();

		return candidateRecords
				.map(record -> fromCandidateRecord(record).get())
				.collect(Collectors.toList());
	}
}