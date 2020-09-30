/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog;

import ASD_Utilities.AssetCatalog.CatalogItem;
import ASD_Utilities.AssetCatalog.SpawnSlot;

/**
 *
 * @author ryan
 */
public class TerrainTextureSpawnSlot implements SpawnSlot {

    public CatalogItem catalogItem;
    
    @Override
    public CatalogItem getCatalogItem() {
        return catalogItem;
    }

    @Override
    public void update(float tpf) {
    
    }
    
}
