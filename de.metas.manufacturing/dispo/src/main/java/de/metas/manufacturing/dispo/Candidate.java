package de.metas.manufacturing.dispo;

import java.math.BigDecimal;
import java.util.Date;

import org.adempiere.util.lang.ITableRecordReference;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_Product;

import de.metas.quantity.Quantity;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

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

@Builder
@Getter
@ToString
public class Candidate
{
	public enum Type
	{
		DEMAND, SUPPLY, STOCK
	};

	/**
	 * the supply type is relevant if the candidate's type is {@link Type#SUPPLY}.
	 *
	 * @author metas-dev <dev@metasfresh.com>
	 *
	 */
	public enum SupplyType
	{
		DISTRIBUTION, PRODUCTION

		/* PURCHASE this comes later */
	};

	@NonNull
	private final I_M_Product product;

	@NonNull
	private final I_M_Locator locator;

	@NonNull
	private final Quantity quantity;

	private final BigDecimal qtyDelta;

	@NonNull
	private final Type type;

	private final SupplyType supplyType;

	private final ITableRecordReference referencedRecord;

	@NonNull
	private final Date projectedDate;

	public Candidate withOtherQuantity(final Quantity quantity)
	{
		return builder()
				.locator(locator)
				.product(product)
				.projectedDate(projectedDate)
				.type(type)
				.supplyType(supplyType)
				.quantity(quantity)
				.build();
	}
}
