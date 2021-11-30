/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import data.DataAccess;
import data.DataResult;
import data.DatabaseConnection;
import meta.Flag;
import meta.Indicateur;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

/**
 *
 * @author SylvainPro
 */
public class IndicateurResponse {
    
    private final String code;
    private final String libelle;
    private final String unit;
    private Double value;
    private String flag;
    private Double uncertainty;
    private String year;
    private String source;
    private String info;
    private Flag indice;
        
    /* ---------- Constructors ---------- */
    
    // Default constructor (used published data or returning value null)
    public IndicateurResponse (DatabaseConnection connection,
                               Indicateur indicateur,
                               UniteLegaleResponse uniteLegale) throws SQLException 
    {
        code = indicateur.getCode();
        libelle = indicateur.getLibelle();
        unit = indicateur.getUnit();
        DataResult rs = DataAccess.getIndicateurData(connection, indicateur, uniteLegale);
        if (rs!=null) 
        {
            this.value = round(rs.value,indicateur.getPrecision());
            this.flag = Flag.PUBLICATION.getCode();
            this.uncertainty = round(rs.uncertainty,0);
            this.year = rs.time;
            this.source = rs.source;
            this.info = rs.info;
            this.indice = Flag.getFlag(flag);
        }
    }
    
    // Full constructor (all attributs in parameters)
    public IndicateurResponse (Indicateur indicateur,
                               Double value,
                               String flag,
                               Double uncertainty,
                               String year,
                               String source,String info) 
    {
        this.code = indicateur.getCode();
        this.libelle = indicateur.getLibelle();
        this.unit = indicateur.getUnit();
        this.value = round(value,indicateur.getPrecision());
        this.flag = flag;
        this.uncertainty = round(uncertainty,0);
        this.year = year;
        this.source = source;
        this.info = info;
        this.indice = Flag.getFlag(flag);
    }
        
    /* ----- SETTERS ----- */

    public void setValue(Double value) {
        this.value = value;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public void setUncertainty(Double uncertainty) {
        this.uncertainty = uncertainty;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setInfo(String info) {
        this.info = info;
    }
  
    /* ----- GETTERS ----- */

    public String getCode() {
        return code;
    }
    public String getLibelle() {
        return libelle;
    }
    public String getUnit() {
        return unit;
    }
    public Double getValue() {
        return value;
    }
    public String getFlag() {
        return flag;
    }
    public Double getUncertainty() {
        return uncertainty;
    }
    public String getYear() {
        return year;
    }
    public String getSource() {
        return source;
    } 
    public String getInfo() {
        return info;
    }
    public String getLibelleFlag() {
        return indice.getLibelle();
    }    
    public Boolean isValueDeclared () {
        return indice.isDeclared();
    }
        
    /* ----- UTILS ----- */
    
    private static Double round(Double value,Integer precision) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(precision,RoundingMode.FLOOR);
        return bd.doubleValue();
    }
    
}
