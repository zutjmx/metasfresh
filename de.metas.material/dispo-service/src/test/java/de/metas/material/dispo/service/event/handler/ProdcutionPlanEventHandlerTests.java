package de.metas.material.dispo.service.event.handler;

import static org.adempiere.model.InterfaceWrapperHelper.newInstance;
import static org.adempiere.model.InterfaceWrapperHelper.save;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.adempiere.test.AdempiereTestHelper;
import org.adempiere.test.AdempiereTestWatcher;
import org.adempiere.util.time.SystemTime;
import org.compiere.model.I_AD_Org;
import org.compiere.util.TimeUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.metas.material.dispo.CandidateRepository;
import de.metas.material.dispo.CandidateService;
import de.metas.material.dispo.CandidateSpecification.Type;
import de.metas.material.dispo.DispoTestUtils;
import de.metas.material.dispo.model.I_MD_Candidate;
import de.metas.material.dispo.service.candidatechange.CandidateChangeService;
import de.metas.material.dispo.service.candidatechange.StockCandidateService;
import de.metas.material.dispo.service.candidatechange.handler.DemandCandiateHandler;
import de.metas.material.dispo.service.candidatechange.handler.SupplyCandiateHandler;
import de.metas.material.event.EventDescr;
import de.metas.material.event.MaterialEventService;
import de.metas.material.event.pporder.PPOrder;
import de.metas.material.event.pporder.PPOrderLine;
import de.metas.material.event.pporder.ProductionPlanEvent;
import mockit.Mocked;

/*
 * #%L
 * metasfresh-material-dispo
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

public class ProdcutionPlanEventHandlerTests
{
	@Rule
	public final AdempiereTestWatcher testWatcher = new AdempiereTestWatcher();
	
	public static final Date t0 = SystemTime.asDate();

	private static final Date t1 = TimeUtil.addMinutes(t0, 10);

	private static final Date t2 = TimeUtil.addMinutes(t0, 20);

	public static final int rawProduct1Id = 50;

	public static final int rawProduct2Id = 55;

	public static final int productId = 40;

	public static final int intermediateWarehouseId = 20;

	private final BigDecimal eleven = BigDecimal.TEN.add(BigDecimal.ONE);

	private I_AD_Org org;

	@Mocked
	private MaterialEventService materialEventService;

	private ProductionPlanEventHandler productionPlanEventHandler;

	@Before
	public void init()
	{
		AdempiereTestHelper.get().init();

		org = newInstance(I_AD_Org.class);
		save(org);

		final CandidateRepository candidateRepository = new CandidateRepository();
		final StockCandidateService stockCandidateService = new StockCandidateService(candidateRepository);
		
		final CandidateChangeService candidateChangeHandler = new CandidateChangeService(ImmutableList.of(
				new SupplyCandiateHandler(candidateRepository, materialEventService, stockCandidateService),
				new DemandCandiateHandler(candidateRepository, materialEventService, stockCandidateService)
				));
		
		final CandidateService candidateService = new CandidateService(
				candidateRepository, 
				MaterialEventService.createLocalServiceThatIsReadyToUse());

		productionPlanEventHandler = new ProductionPlanEventHandler(candidateChangeHandler, candidateService);
	}

	@Test
	public void testProductionPlanEvent()
	{
		final ProductionPlanEvent productionPlanEvent = createProductionPlanEvent();
		perform_testProductionPlanEvent(productionPlanEvent);
	}

	@Test
	public void testProductionPlanEvent_invoke_twice()
	{
		final ProductionPlanEvent productionPlanEvent = createProductionPlanEvent();
		perform_testProductionPlanEvent(productionPlanEvent);
		perform_testProductionPlanEvent(productionPlanEvent);
	}

	private void perform_testProductionPlanEvent(final ProductionPlanEvent productionPlanEvent)
	{
		productionPlanEventHandler.handleProductionPlanEvent(productionPlanEvent);

		assertThat(DispoTestUtils.filter(Type.SUPPLY).size()).isEqualTo(1); //
		assertThat(DispoTestUtils.filter(Type.DEMAND)).hasSize(2); // we have two different inputs
		assertThat(DispoTestUtils.filter(Type.STOCK)).hasSize(3); // one stock record per supply, one per demand

		final I_MD_Candidate t2Stock = DispoTestUtils.filter(Type.STOCK, t2).get(0);
		assertThat(t2Stock.getQty()).isEqualByComparingTo(BigDecimal.ONE);
		assertThat(t2Stock.getM_Product_ID()).isEqualTo(productId);
		assertThat(t2Stock.getMD_Candidate_GroupId()).isGreaterThan(0); // stock candidates have their own groupIds too
		assertThat(t2Stock.getMD_Candidate_Parent_ID(), lessThanOrEqualTo(0));

		final I_MD_Candidate t2Supply = DispoTestUtils.filter(Type.SUPPLY, t2).get(0);
		assertThat(t2Supply.getQty()).isEqualByComparingTo(BigDecimal.ONE);
		assertThat(t2Supply.getM_Product_ID()).isEqualTo(productId);
		assertThat(t2Supply.getMD_Candidate_Parent_ID()).isEqualTo(t2Stock.getMD_Candidate_ID());
		assertThat(t2Supply.getMD_Candidate_GroupId()).isNotEqualTo(t2Stock.getMD_Candidate_GroupId()); // stock candidates' groupIds are different from supply/demand groups' groupIds

		final int supplyDemandGroupId = t2Supply.getMD_Candidate_GroupId();
		assertThat(supplyDemandGroupId).isGreaterThan(0);

		final I_MD_Candidate t1Product1Demand = DispoTestUtils.filter(Type.DEMAND, t1, rawProduct1Id).get(0);
		assertThat(t1Product1Demand.getQty()).isEqualByComparingTo(BigDecimal.TEN);
		assertThat(t1Product1Demand.getM_Product_ID()).isEqualTo(rawProduct1Id);
		assertThat(t1Product1Demand.getMD_Candidate_GroupId()).isEqualTo(supplyDemandGroupId);
		// no parent relationship between production supply and demand because it can be m:n
		// assertThat(t1Product1Demand.getMD_Candidate_Parent_ID()).isEqualTo(t2Supply.getMD_Candidate_ID());

		final I_MD_Candidate t1Product1Stock = DispoTestUtils.filter(Type.STOCK, t1, rawProduct1Id).get(0);
		assertThat(t1Product1Stock.getQty()).isEqualByComparingTo(BigDecimal.TEN.negate());
		assertThat(t1Product1Stock.getM_Product_ID()).isEqualTo(rawProduct1Id);
		assertThat(t1Product1Stock.getMD_Candidate_GroupId()).isGreaterThan(0);  // stock candidates have their own groupIds too
		assertThat(t1Product1Stock.getMD_Candidate_GroupId()).isNotEqualTo(supplyDemandGroupId);  // stock candidates' groupIds are different from supply/demand groups' groupIds
		assertThat(t1Product1Stock.getMD_Candidate_GroupId()).isNotEqualTo(t2Stock.getMD_Candidate_GroupId());  // stock candidates' groupIds are different if they are about different products or warehouses

		assertThat(t1Product1Stock.getMD_Candidate_Parent_ID()).isEqualTo(t1Product1Demand.getMD_Candidate_ID());

		final I_MD_Candidate t1Product2Demand = DispoTestUtils.filter(Type.DEMAND, t1, rawProduct2Id).get(0);
		assertThat(t1Product2Demand.getQty()).isEqualByComparingTo(eleven);
		assertThat(t1Product2Demand.getM_Product_ID()).isEqualTo(rawProduct2Id);
		assertThat(t1Product2Demand.getMD_Candidate_GroupId()).isEqualTo(supplyDemandGroupId);
		// no parent relationship between production supply and demand because it can be m:n
		// assertThat(t1Product2Demand.getMD_Candidate_Parent_ID()).isEqualTo(t2Supply.getMD_Candidate_ID());

		final I_MD_Candidate t1Product2Stock = DispoTestUtils.filter(Type.STOCK, t1, rawProduct2Id).get(0);
		assertThat(t1Product2Stock.getQty()).isEqualByComparingTo(eleven.negate());
		assertThat(t1Product2Stock.getM_Product_ID()).isEqualTo(rawProduct2Id);
		assertThat(t1Product2Stock.getMD_Candidate_GroupId()).isGreaterThan(0); // stock candidates have their own groupIds too
		assertThat(t1Product2Stock.getMD_Candidate_Parent_ID()).isEqualTo(t1Product2Demand.getMD_Candidate_ID());
		assertThat(t1Product2Stock.getMD_Candidate_GroupId()).isNotEqualTo(t1Product1Stock.getMD_Candidate_GroupId());  // stock candidates' groupIds are different if they are about different products or warehouses
	}

	private ProductionPlanEvent createProductionPlanEvent()
	{
		final ProductionPlanEvent productionPlanEvent = ProductionPlanEvent.builder()
				.eventDescr(new EventDescr(org.getAD_Client_ID(), org.getAD_Org_ID()))
				.materialDemandDescr(Optional.empty())
				.ppOrder(PPOrder.builder()
						.orgId(org.getAD_Org_ID())
						.datePromised(t2)
						.dateStartSchedule(t1)
						.productId(productId)
						.quantity(BigDecimal.ONE)
						.warehouseId(intermediateWarehouseId)
						.plantId(120)
						.uomId(130)
						.productPlanningId(140)
						.line(PPOrderLine.builder()
								.description("descr1")
								.productId(rawProduct1Id)
								.qtyRequired(BigDecimal.TEN)
								.productBomLineId(1020)
								.receipt(false)
								.build())
						.line(PPOrderLine.builder()
								.description("descr2")
								.productId(rawProduct2Id)
								.qtyRequired(eleven)
								.productBomLineId(1030)
								.receipt(false)
								.build())
						.build())
				.build();
		return productionPlanEvent;
	}

}
