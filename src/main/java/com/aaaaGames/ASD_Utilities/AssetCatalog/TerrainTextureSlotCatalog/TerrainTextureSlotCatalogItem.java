/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog;

import ASD_Utilities.AssetCatalog.CatalogItem;

/**
 *
 * @author ryan
 */
public class TerrainTextureSlotCatalogItem extends CatalogItem{
    
    
    public TerrainTextureSlot terrainTextureSlot;
    public TerrainTextureSlot getTerrainTextureSlot() {    return terrainTextureSlot;   }
    
    public TerrainTextureSlotCatalogItem(TerrainTextureSlot terrainTextureSlot, TerrainTextureSlotCatalog catalog){
        super("", "", catalog);
        
        this.terrainTextureSlot = terrainTextureSlot;
        this.assetName = terrainTextureSlot.getName();
        displayName = assetName;
    }

    @Override
    public String getAssetKeyString() {
        return terrainTextureSlot.getAlbedoAssetKey();
    }

    public String displayName = null;
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String dn){ displayName = dn; }
    
}
