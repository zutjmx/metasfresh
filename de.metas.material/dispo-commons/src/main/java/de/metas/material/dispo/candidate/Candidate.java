package de.metas.material.dispo.candidate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;

import de.metas.material.dispo.CandidateSpecification.Status;
import de.metas.material.dispo.CandidateSpecification.SubType;
import de.metas.material.dispo.CandidateSpecification.Type;
import de.metas.material.dispo.CandidatesQuery;
import de.metas.material.event.EventDescr;
import de.metas.material.event.MaterialDescriptor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Wither;

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

@Value
@Builder
@Wither
public class Candidate
{
	public static CandidateBuilder builderForEventDescr(@NonNull final EventDescr eventDescr)
	{
		return Candidate.builder()
				.clientId(eventDescr.getClientId())
				.orgId(eventDescr.getOrgId());
	}

	int clientId;

	int orgId;

	@NonNull
	Type type;

	/**
	 * Should be {@code null} for stock candidates.
	 */
	SubType subType;

	Status status;

	// private final TableRecordReference reference;

	int id;

	/**
	 * A supply candidate has a stock candidate as its parent. A demand candidate has a stock candidate as its child.
	 */
	int parentId;

	/**
	 * A supply candidate and its corresponding demand candidate are associated by a common group id.
	 */
	int groupId;

	int seqNo;

	@NonNull
	MaterialDescriptor materialDescr;

	/**
	 * Used for additional infos if this candidate has the sub type {@link SubType#PRODUCTION}.
	 */
	ProductionDetail productionDetail;

	/**
	 * Used for additional infos if this candidate has the sub type {@link SubType#DISTRIBUTION}.
	 */
	DistributionDetail distributionDetail;

	/**
	 * Used for additional infos if this candidate relates to particular demand
	 */
	DemandDetail demandDetail;

	@Singular
	List<TransactionDetail> transactionDetails;

	/**
	 * Does not create a parent segment, even if this candidate has a parent.
	 *
	 * @return
	 */
	public CandidatesQuery.CandidatesQueryBuilder mkSegmentBuilder()
	{
		return CandidatesQuery.builder().materialDescr(materialDescr.withoutQuantity());
	}

	public BigDecimal getQuantity()
	{
		return materialDescr.getQuantity();
	}

	public Candidate withQuantity(@NonNull final BigDecimal quantity)
	{
		return withMaterialDescr(materialDescr.withQuantity(quantity));
	}

	public Candidate withDate(@NonNull final Date date)
	{
		return withMaterialDescr(materialDescr.withDate(date));
	}

	public Candidate withWarehouseId(final int warehouseId)
	{
		return withMaterialDescr(materialDescr.withWarehouseId(warehouseId));
	}

	public int getEffectiveGroupId()
	{
		if (type == Type.STOCK)
		{
			return 0;
		}
		if (groupId > 0)
		{
			return groupId;
		}
		return id;
	}

	public Date getDate()
	{
		return materialDescr.getDate();
	}

	public int getProductId()
	{
		return materialDescr.getProductId();
	}

	public int getWarehouseId()
	{
		return materialDescr.getWarehouseId();
	}

	private Candidate(final int clientId, final int orgId,
			@NonNull final Type type,
			final SubType subType,
			final Status status,
			final int id,
			final int parentId,
			final int groupId,
			final int seqNo,
			@NonNull final MaterialDescriptor materialDescriptor,
			final ProductionDetail productionDetail,
			final DistributionDetail distributionDetail,
			final DemandDetail demandDetail,
			final List<TransactionDetail> transactionDetails)
	{
		this.clientId = clientId;
		this.orgId = orgId;
		this.type = type;
		this.subType = subType;
		this.status = status;
		this.id = id;
		this.parentId = parentId;
		this.groupId = groupId;
		this.seqNo = seqNo;

		Preconditions.checkArgument(materialDescriptor.isComplete(),
				"Given parameter materialDescriptor needs to have iscomplete==true; materialDescriptor=%s", materialDescriptor);
		this.materialDescr = materialDescriptor;

		this.productionDetail = productionDetail;
		this.distributionDetail = distributionDetail;
		this.demandDetail = demandDetail;

		for (final TransactionDetail transactionDetail : transactionDetails)
		{
			Preconditions.checkArgument(transactionDetail == null || transactionDetail.isComplete(),
					"Every element from the given parameter transactionDetails needs to have iscomplete==true; transactionDetail=%s", 
					transactionDetail);
		}
		this.transactionDetails = transactionDetails;
	}
}
