/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog;

import ASD_Utilities.ASDUCoreState;
import ASD_Utilities.AssetCatalog.DecalCatalog.DecalCatalog;
import ASD_Utilities.AssetCatalog.ModelCatalog.ModelCatalog;
import ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog.TerrainTextureSlotCatalog;
import ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog.TextureSlotManager;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;


/**
 *
 * @author ryan
 */
public class CatalogManager {
    
    
    
    
    private ModelCatalog modelCatalog;
    private DecalCatalog decalCatalog;
    private TerrainTextureSlotCatalog terrainTextureSlotCatalog;
    
    public ModelCatalog getModelCatalog(){ return modelCatalog; }
    public DecalCatalog getDecalCatalog(){ return decalCatalog; }
    public TerrainTextureSlotCatalog getTerrainTextureSlotCatalog(){ return terrainTextureSlotCatalog; }
      
    public SimpleApplication app;
    
    

    public void setAssetDirectory(String assetDirectory) {    
        //anything extra need done when setting a new assetDirectory?
        this.assetDirectory = assetDirectory;    }
    

    public TextureSlotManager textureSlotManager;
    
    
    public CatalogManager(SimpleApplication app, String assetDirectory, ASDUCoreState asduCore){
        
        this.asduCore = asduCore;
     //catalogs are loaded and filled in parent Catalog class based on the .json file extension specified 
       
            
     
     
     
        setAssetDirectory(assetDirectory);
        this.app = app;
       
        
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
       float screenWidth = (float) screenSize.getWidth() * .99f;        
       float screenHeight = (float) screenSize.getHeight()  * .925f;
        
      
        textureSlotManager = new TextureSlotManager(this, screenWidth, screenHeight);
        
        
        
        modelCatalog = new ModelCatalog(this);
        decalCatalog = new DecalCatalog(this);
        terrainTextureSlotCatalog = new TerrainTextureSlotCatalog(this);
      
        
        setActiveCatalog(modelCatalog);
        
            //      app.enqueue(() -> {
            textureSlotManager.fillTextureSlotsFromCatalog();
    //      });

    
        
        
        
    }
    


    public void cleanUp() {
        if(modelCatalog != null){
            modelCatalog.saveCatalogItems();
        }
        if(decalCatalog != null){
            decalCatalog.saveCatalogItems();
        }
        if(terrainTextureSlotCatalog != null){
            terrainTextureSlotCatalog.saveCatalogItems();
        }
        
    
    }

    
    public void unregisterTag(AssetTag tag) {    
        activeCatalog.unregisterTag(tag);
    }
    
    
    
     //returns null if tag already exists
    public AssetTag registerNewTag(String tagName){
       return activeCatalog.registerNewTag(tagName);
       
    }
    
    //returns null if tag doesnt exist 
    public AssetTag getTag(String tagName){
        return activeCatalog.getTag(tagName);
    }
    
    public ArrayList getValidTagsForItem(String[] tagStrings){
        return activeCatalog.getValidTagsForItem(tagStrings);
        
    }
    
    
    private Catalog activeCatalog;
    public Catalog getActiveCatalog(){ return activeCatalog; }
    
    public void setActiveCatalog(Catalog newActiveCatalog){
        activeCatalog = newActiveCatalog;
        
    }

    public ArrayList<CatalogItem> getcatalogItemsFromActiveCatalog() {
        return activeCatalog.getCatalogItems();
    
    }
   
    
    
    public void addItemToCatalog(CatalogItem newCatalogItem, Catalog catalogToAddTo){
        catalogToAddTo.addNewCatalogItem(newCatalogItem);
        
    }

    public void removeItemFromCatalog(CatalogItem catalogItemToRemove, Catalog catalogToRemoveFrom){
        catalogToRemoveFrom.removeCatalogItem(catalogItemToRemove);
    }

    
    
    public void applyMaterialVariantToNode(Node nodeToCheck, String materialVariantExtensionString){
        SafeArrayList<Spatial> children = (SafeArrayList<Spatial>) nodeToCheck.getChildren();

        for(int s = 0; s < children.size(); s ++){
            Spatial childSpatial = children.get(s);

            if(childSpatial instanceof Node){
                applyMaterialVariantToNode((Node) childSpatial, materialVariantExtensionString);
            }
            else if(childSpatial instanceof Geometry){
                Geometry geoToCheck = (Geometry) childSpatial;
                Material geoMat = geoToCheck.getMaterial();
                if(geoMat != null){
                    String originalMatString = geoMat.getAssetName();
                    originalMatString = originalMatString.substring(0, originalMatString.length() - 4);
                    
                    String materialVariantFullString = originalMatString + "_" + materialVariantExtensionString + ".j3m";
                    Material variantMat = app.getAssetManager().loadMaterial(materialVariantFullString);
                    geoToCheck.setMaterial(variantMat);
                    
                }
            }

        }
        
        
    }
    
    public void removeMaterialVariantFromNode(Node nodeToCheck, String materialVariantExtensionString){
        SafeArrayList<Spatial> children = (SafeArrayList<Spatial>) nodeToCheck.getChildren();

        for(int s = 0; s < children.size(); s ++){
            Spatial childSpatial = children.get(s);

            if(childSpatial instanceof Node){
                removeMaterialVariantFromNode((Node) childSpatial, materialVariantExtensionString);
            }
            else if(childSpatial instanceof Geometry){
                Geometry geoToCheck = (Geometry) childSpatial;
                Material geoMat = geoToCheck.getMaterial();
                if(geoMat != null){
                    String variantMatString = geoMat.getAssetName();
                    if(variantMatString != null){
                              
                        String originalMatString = variantMatString.substring(0, variantMatString.length() - (4 + materialVariantExtensionString.length()));

                        originalMatString += ".j3m";

                        Material originalMat = app.getAssetManager().loadMaterial(originalMatString);                        

                        geoToCheck.setMaterial(originalMat);
                    }                    
                }
            }

        }
        
        
    }

    public SimpleApplication getApp() {        return app;    }

    public String assetDirectory;
    public String getAssetDirectory() { return assetDirectory; }

    public TextureSlotManager getTextureSlotManager() { return textureSlotManager;}

    public ASDUCoreState asduCore;
    public ASDUCoreState getASDUCore() {   return asduCore; }

    
    
    public void update(float tpf) {
        
        if(textureSlotManager != null){
             textureSlotManager.update(tpf);
        }
       
    
    }

    
    
}
