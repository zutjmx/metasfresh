/** Generated Model - DO NOT CHANGE */
package de.metas.manufacturing.dispo.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.Env;

/**
 * Generated Model for MD_Candidate
 * 
 * @author Adempiere (generated)
 */
@SuppressWarnings("javadoc")
public class X_MD_Candidate extends org.compiere.model.PO implements I_MD_Candidate, org.compiere.model.I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 2069065785L;

	/** Standard Constructor */
	public X_MD_Candidate(final Properties ctx, final int MD_Candidate_ID, final String trxName)
	{
		super(ctx, MD_Candidate_ID, trxName);
		/**
		 * if (MD_Candidate_ID == 0)
		 * {
		 * setC_UOM_ID (0);
		 * setDateProjected (new Timestamp( System.currentTimeMillis() ));
		 * setM_Locator_ID (0);
		 * setM_Product_ID (0);
		 * setMD_Candidate_ID (0);
		 * setMD_Candidate_Type (null);
		 * setQty (Env.ZERO);
		 * }
		 */
	}

	/** Load Constructor */
	public X_MD_Candidate(final Properties ctx, final ResultSet rs, final String trxName)
	{
		super(ctx, rs, trxName);
	}

	/** Load Meta Data */
	@Override
	protected org.compiere.model.POInfo initPO(final Properties ctx)
	{
		final org.compiere.model.POInfo poi = org.compiere.model.POInfo.getPOInfo(ctx, Table_Name, get_TrxName());
		return poi;
	}

	@Override
	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
	{
		return get_ValueAsPO(COLUMNNAME_AD_Table_ID, org.compiere.model.I_AD_Table.class);
	}

	@Override
	public void setAD_Table(final org.compiere.model.I_AD_Table AD_Table)
	{
		set_ValueFromPO(COLUMNNAME_AD_Table_ID, org.compiere.model.I_AD_Table.class, AD_Table);
	}

	/**
	 * Set DB-Tabelle.
	 * 
	 * @param AD_Table_ID
	 *            Database Table information
	 */
	@Override
	public void setAD_Table_ID(final int AD_Table_ID)
	{
		if (AD_Table_ID < 1)
		{
			set_Value(COLUMNNAME_AD_Table_ID, null);
		}
		else
		{
			set_Value(COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
		}
	}

	/**
	 * Get DB-Tabelle.
	 * 
	 * @return Database Table information
	 */
	@Override
	public int getAD_Table_ID()
	{
		final Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
		{
			return 0;
		}
		return ii.intValue();
	}

	@Override
	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
	{
		return get_ValueAsPO(COLUMNNAME_C_UOM_ID, org.compiere.model.I_C_UOM.class);
	}

	@Override
	public void setC_UOM(final org.compiere.model.I_C_UOM C_UOM)
	{
		set_ValueFromPO(COLUMNNAME_C_UOM_ID, org.compiere.model.I_C_UOM.class, C_UOM);
	}

	/**
	 * Set Maßeinheit.
	 * 
	 * @param C_UOM_ID
	 *            Maßeinheit
	 */
	@Override
	public void setC_UOM_ID(final int C_UOM_ID)
	{
		if (C_UOM_ID < 1)
		{
			set_Value(COLUMNNAME_C_UOM_ID, null);
		}
		else
		{
			set_Value(COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
		}
	}

	/**
	 * Get Maßeinheit.
	 * 
	 * @return Maßeinheit
	 */
	@Override
	public int getC_UOM_ID()
	{
		final Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
		{
			return 0;
		}
		return ii.intValue();
	}

	/**
	 * Set Plandatum.
	 * 
	 * @param DateProjected Plandatum
	 */
	@Override
	public void setDateProjected(final java.sql.Timestamp DateProjected)
	{
		set_Value(COLUMNNAME_DateProjected, DateProjected);
	}

	/**
	 * Get Plandatum.
	 * 
	 * @return Plandatum
	 */
	@Override
	public java.sql.Timestamp getDateProjected()
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_DateProjected);
	}

	@Override
	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException
	{
		return get_ValueAsPO(COLUMNNAME_M_Locator_ID, org.compiere.model.I_M_Locator.class);
	}

	@Override
	public void setM_Locator(final org.compiere.model.I_M_Locator M_Locator)
	{
		set_ValueFromPO(COLUMNNAME_M_Locator_ID, org.compiere.model.I_M_Locator.class, M_Locator);
	}

	/**
	 * Set Lagerort.
	 * 
	 * @param M_Locator_ID
	 *            Lagerort im Lager
	 */
	@Override
	public void setM_Locator_ID(final int M_Locator_ID)
	{
		if (M_Locator_ID < 1)
		{
			set_Value(COLUMNNAME_M_Locator_ID, null);
		}
		else
		{
			set_Value(COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
		}
	}

	/**
	 * Get Lagerort.
	 * 
	 * @return Lagerort im Lager
	 */
	@Override
	public int getM_Locator_ID()
	{
		final Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
		{
			return 0;
		}
		return ii.intValue();
	}

	@Override
	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return get_ValueAsPO(COLUMNNAME_M_Product_ID, org.compiere.model.I_M_Product.class);
	}

	@Override
	public void setM_Product(final org.compiere.model.I_M_Product M_Product)
	{
		set_ValueFromPO(COLUMNNAME_M_Product_ID, org.compiere.model.I_M_Product.class, M_Product);
	}

	/**
	 * Set Produkt.
	 * 
	 * @param M_Product_ID
	 *            Produkt, Leistung, Artikel
	 */
	@Override
	public void setM_Product_ID(final int M_Product_ID)
	{
		if (M_Product_ID < 1)
		{
			set_Value(COLUMNNAME_M_Product_ID, null);
		}
		else
		{
			set_Value(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
		}
	}

	/**
	 * Get Produkt.
	 * 
	 * @return Produkt, Leistung, Artikel
	 */
	@Override
	public int getM_Product_ID()
	{
		final Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
		{
			return 0;
		}
		return ii.intValue();
	}

	/**
	 * Set Dispositionskandidat.
	 * 
	 * @param MD_Candidate_ID Dispositionskandidat
	 */
	@Override
	public void setMD_Candidate_ID(final int MD_Candidate_ID)
	{
		if (MD_Candidate_ID < 1)
		{
			set_ValueNoCheck(COLUMNNAME_MD_Candidate_ID, null);
		}
		else
		{
			set_ValueNoCheck(COLUMNNAME_MD_Candidate_ID, Integer.valueOf(MD_Candidate_ID));
		}
	}

	/**
	 * Get Dispositionskandidat.
	 * 
	 * @return Dispositionskandidat
	 */
	@Override
	public int getMD_Candidate_ID()
	{
		final Integer ii = (Integer)get_Value(COLUMNNAME_MD_Candidate_ID);
		if (ii == null)
		{
			return 0;
		}
		return ii.intValue();
	}

	/**
	 * MD_Candidate_Type AD_Reference_ID=540707
	 * Reference name: MD_Candidate_Type
	 */
	public static final int MD_CANDIDATE_TYPE_AD_Reference_ID = 540707;
	/** STOCK = STOCK */
	public static final String MD_CANDIDATE_TYPE_STOCK = "STOCK";
	/** DEMAND = DEMAND */
	public static final String MD_CANDIDATE_TYPE_DEMAND = "DEMAND";
	/** SUPPLY = SUPPLY */
	public static final String MD_CANDIDATE_TYPE_SUPPLY = "SUPPLY";

	/**
	 * Set Typ.
	 * 
	 * @param MD_Candidate_Type Typ
	 */
	@Override
	public void setMD_Candidate_Type(final java.lang.String MD_Candidate_Type)
	{

		set_Value(COLUMNNAME_MD_Candidate_Type, MD_Candidate_Type);
	}

	/**
	 * Get Typ.
	 * 
	 * @return Typ
	 */
	@Override
	public java.lang.String getMD_Candidate_Type()
	{
		return (java.lang.String)get_Value(COLUMNNAME_MD_Candidate_Type);
	}

	/**
	 * Set Menge.
	 * 
	 * @param Qty
	 *            Menge
	 */
	@Override
	public void setQty(final java.math.BigDecimal Qty)
	{
		set_Value(COLUMNNAME_Qty, Qty);
	}

	/**
	 * Get Menge.
	 * 
	 * @return Menge
	 */
	@Override
	public java.math.BigDecimal getQty()
	{
		final BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
		{
			return Env.ZERO;
		}
		return bd;
	}

	/**
	 * Set Datensatz-ID.
	 * 
	 * @param Record_ID
	 *            Direct internal record ID
	 */
	@Override
	public void setRecord_ID(final int Record_ID)
	{
		if (Record_ID < 0)
		{
			set_Value(COLUMNNAME_Record_ID, null);
		}
		else
		{
			set_Value(COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
		}
	}

	/**
	 * Get Datensatz-ID.
	 * 
	 * @return Direct internal record ID
	 */
	@Override
	public int getRecord_ID()
	{
		final Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
		{
			return 0;
		}
		return ii.intValue();
	}
}