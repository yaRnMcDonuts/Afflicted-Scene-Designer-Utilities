/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog;

import ASD_Utilities.AssetCatalog.Catalog;
import ASD_Utilities.AssetCatalog.CatalogItem;
import ASD_Utilities.AssetCatalog.CatalogManager;
import ASD_Utilities.AssetCatalog.AddCatalogItemTool;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author ryan
 */
public class TerrainTextureSlotCatalog extends Catalog {
    
    
    @Override
     public AddCatalogItemTool getAddCatalogItemTool(){ 
        if(addCatalogItemTool == null){
            addCatalogItemTool = new AddTerrainTextureSlotCatalogItemTool(catalogManager);
        } 
        return addCatalogItemTool; }
    
    public TerrainTextureSlotCatalog(CatalogManager cm) {
        super(cm, "TerrainTextureSlot");
        
      
        
        
    //    this.loadCatalogFromNode();
        
    }

    @Override
    public void addNewCatalogItem(CatalogItem item) {
        super.addNewCatalogItem(item); 
        
        
    }

    public TerrainTextureSlotCatalogItem addNewTerrainTextureSlotCatalogItem(TerrainTextureSlot newTextureSlot) {
    
        boolean alreadyExists = false;
        
        for(int i = 0; i < catalogItems.size(); i++){
            
            TerrainTextureSlotCatalogItem existingItem = (TerrainTextureSlotCatalogItem) catalogItems.get(i);
            TerrainTextureSlot existingSlot = existingItem.getTerrainTextureSlot();
            
            if(existingSlot.getName().equals(newTextureSlot.getName())){
                
                alreadyExists = true;             
                i = catalogItems.size() + 2;
                
                return existingItem;
            }
            
        }
        
        
        
        if(!alreadyExists){
            
            TerrainTextureSlotCatalogItem newItem = new TerrainTextureSlotCatalogItem(newTextureSlot, this);
            newItem.setName(newTextureSlot.getName());
            
            addNewCatalogItem(newItem);
            
            return newItem;
        }
        
        
        return null;
    }

    
    
  //override save/load for this specific catalog, as a TerrainTextureSlot includes more than one texture asset reference and needs to save/load more data per slot than the standard single assetpath catalog item
     @Override
        public void saveCatalogItems(){
        
        
        Node catalogSaveNode = new Node("TerrainTextureSlot_Catalog_SaveNode");
        
        int totalItemCount = catalogItems.size();

        catalogSaveNode.setUserData("CatalogName", catalogName);
        catalogSaveNode.setUserData("TotalItemCount", totalItemCount);


        for(int c = 0; c < catalogItems.size(); c++){
            TerrainTextureSlotCatalogItem item = (TerrainTextureSlotCatalogItem) catalogItems.get(c);
            TerrainTextureSlot textureSlot = item.getTerrainTextureSlot();


            String itemName = textureSlot.getName();           
            String itemDisplayName = item.getDisplayName();

            String tagsString = item.getAllTagsAsString();

            Texture albedoTexture = textureSlot.getAlbedoTexture();
            String albedoMapAssetKey = "";
            if(albedoTexture != null){
                albedoMapAssetKey = albedoTexture.getKey().getName();
            }
            else{
                albedoMapAssetKey = textureSlot.getAlbedoAssetKey();
            }

            Texture normalTexture = textureSlot.getNormalMapTexture();
            String normalMapAssetKey = "";
            if(normalTexture != null){
                normalMapAssetKey = normalTexture.getKey().getName();
            }
            else{
                normalMapAssetKey = textureSlot.getNormalMapAssetKey();
            }

            Texture parallaxTexture = textureSlot.getParallaxTexture();
            String parallaxMapAssetKey = "";
            if(parallaxTexture != null){
                parallaxMapAssetKey = parallaxTexture.getKey().getName();
            }
            else{
                parallaxMapAssetKey = textureSlot.getParallaxAssetKey();
            }

            Texture packedNormalParallaxTexture = textureSlot.getPackedNormalParallaxTexture();
            String packedNormalParallaxMapAssetKey = "";
            if(packedNormalParallaxTexture != null){
                packedNormalParallaxMapAssetKey = packedNormalParallaxTexture.getKey().getName();
            }
            else{
                packedNormalParallaxMapAssetKey = textureSlot.getPackedNormalParallaxAssetKey();
            }

            Texture roughnessTexture = textureSlot.getRoughnessTexture();
            String roughnessMapAssetKey = "";
            if(roughnessTexture != null){
                roughnessMapAssetKey = roughnessTexture.getKey().getName();
            }
            else{
                roughnessMapAssetKey = textureSlot.getRoughnessAssetKey();
            }

            Texture metallicTexture = textureSlot.getMetallicTexture();
            String metallicMapAssetKey = "";
            if(metallicTexture != null){
                metallicMapAssetKey = metallicTexture.getKey().getName();
            }
            else{
                metallicMapAssetKey = textureSlot.getMetallicAssetKey();
            }

            Texture aoTexture = textureSlot.getAmbientOcclusionTexture();
            String aoMapAssetKey = "";
            if(aoTexture != null){
                aoMapAssetKey = aoTexture.getKey().getName();
            }
            else{
                aoMapAssetKey = textureSlot.getAoAssetKey();
            }

            Texture eiTexture = textureSlot.getEmissiveIntensityTexture();
            String eiMapAssetKey = "";
            if(eiTexture != null){
                eiMapAssetKey = eiTexture.getKey().getName();
            }
            else{
                eiMapAssetKey = textureSlot.getEiAssetKey();
            }

            Texture packedMetallicRoughnessAoEiTexture = textureSlot.getPackedMetallicRoughnessAoEiTexture();
            String packedMetallicRoughnessAoEiMapAssetKey = "";
            if(packedMetallicRoughnessAoEiTexture != null){
                packedMetallicRoughnessAoEiMapAssetKey = packedMetallicRoughnessAoEiTexture.getKey().getName();
            }
            else{
                packedMetallicRoughnessAoEiMapAssetKey = textureSlot.getPackedMetallicRoughnessAoEiAssetKey();
            }



            int albedoTextArrayId = textureSlot.getAlbedoTexArrayId();
            int packedNormalParallaxTextArrayId = textureSlot.getPackedNormalParallaxTexArrayId();
            int packedMetallicRoughnessAoEiTextArrayId = textureSlot.getPackedMetallicRoughnessAoEitextureId();




            float roughnessValue = textureSlot.getRoughnessValue();
            float metallicValue = textureSlot.getMetallicValue();
            float advancedMetallicValue = textureSlot.getAdvancedMetallicValue();
            float advancedRoughnessValue = textureSlot.getAdvancedMetallicValue();
            float scaleValue = textureSlot.getScale();
            float afflictionMode = textureSlot.getAfflictionMode();



            catalogSaveNode.setUserData("item_#" + c + "_Name", itemName);
            catalogSaveNode.setUserData("item_#" + c + "_DisplayName", itemDisplayName);
            catalogSaveNode.setUserData("item_#" + c + "_Tags", tagsString);

            catalogSaveNode.setUserData("item_#" + c + "_AlbedoMap_AssetKey", albedoMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_NormalMap_AssetKey", normalMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_ParallaxMap_AssetKey", parallaxMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_PackedNormalParallaxMap_AssetKey", packedNormalParallaxMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_RoughnessMap_AssetKey", roughnessMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_MetallicMap_AssetKey", metallicMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_AoMap_AssetKey", aoMapAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_EiMap_AssetKey", eiMapAssetKey);        
            catalogSaveNode.setUserData("item_#" + c + "_PackedMetallicRoughnessAoEiMap_AssetKey", packedMetallicRoughnessAoEiMapAssetKey);    

            catalogSaveNode.setUserData("item_#" + c + "_Albedo_TexArrayId", albedoTextArrayId + "");
            catalogSaveNode.setUserData("item_#" + c + "_PackedNormalParallax_TexArrayId", packedNormalParallaxTextArrayId + "");             
            catalogSaveNode.setUserData("item_#" + c + "_PackedMetallicRoughnessAoEi_TexArrayId", packedMetallicRoughnessAoEiTextArrayId + "");               

            catalogSaveNode.setUserData("item_#" + c + "_Scale_Value", scaleValue + "");
            catalogSaveNode.setUserData("item_#" + c + "_Roughness_Value", roughnessValue + "");
            catalogSaveNode.setUserData("item_#" + c + "_Metallic_Value", metallicValue + "");                   
            catalogSaveNode.setUserData("item_#" + c + "_Advanced_Roughness_Value", advancedRoughnessValue + "");
            catalogSaveNode.setUserData("item_#" + c + "_Advanced_Metallic_Value", advancedMetallicValue + ""); 
            catalogSaveNode.setUserData("item_#" + c + "_AfflictionMode_Value", afflictionMode + "");

        }
        
        
        saveFileToNode(catalogSaveNode);

    
    }
    
    @Override
    public void loadCatalogFromNode(){
        
        Node catalogLoadNode = null;
        try{
            catalogLoadNode = (Node) catalogManager.getApp().getAssetManager().loadModel("Catalogs/" + catalogFilName + ".j3o");        
        }
        catch(Exception e){
            //catalog not created yet..
        }

        if(catalogLoadNode != null) {

             int totalItemCount = (int) catalogLoadNode.getUserData("TotalItemCount");
             for(int c = 0; c < totalItemCount; c ++){
                 String textureSlotName, tagsString, itemDisplayName;

                 textureSlotName = (String) catalogLoadNode.getUserData("item_#" + c + "_Name");
                 tagsString = (String) catalogLoadNode.getUserData("item_#" + c + "_Tags");

                 itemDisplayName = (String) catalogLoadNode.getUserData("item_#" + c + "_DisplayName");


                 if(itemDisplayName == null){
                     itemDisplayName = textureSlotName;
                 }



                    String albedoMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_AlbedoMap_AssetKey");         
                    String normalMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_NormalMap_AssetKey");         
                    String parallaxMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_ParallaxMap_AssetKey");       
                    String packedNormalParallaxMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_PackedNormalParallaxMap_AssetKey");       
                    String roughnessMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_RoughnessMap_AssetKey");       
                    String metallicMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_MetallicMap_AssetKey");       
                    String aoMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_AoMap_AssetKey");       
                    String eiMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_EiMap_AssetKey");    
                    String packedMetallicRoughnessAoEiMapAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_PackedMetallicRoughnessAoEiMap_AssetKey");                        


                    String albedoTextArrayIdString = (String) catalogLoadNode.getUserData("item_#" + c + "_Albedo_TexArrayId");
                    String packedNormalParallaxTextArrayIdString = (String) catalogLoadNode.getUserData("item_#" + c + "_PackedNormalParallax_TexArrayId");                
                    String packedMetallicRoughnessAoEiTextArrayIdString = (String) catalogLoadNode.getUserData("item_#" + c + "_PackedMetallicRoughnessAoEi_TexArrayId");                    

                    int albedoTextArrayId = Integer.parseInt(albedoTextArrayIdString);
                    int packedNormalParallaxTextArrayId = Integer.parseInt(packedNormalParallaxTextArrayIdString);
                    int packedMetallicRoughnessAoEiTextArrayId = Integer.parseInt(packedMetallicRoughnessAoEiTextArrayIdString);



                    String scaleValueString = (String) catalogLoadNode.getUserData("item_#" + c + "_Scale_Value");
                    String roughnessValueString = (String) catalogLoadNode.getUserData("item_#" + c + "_Roughness_Value");
                    String metallicValueString = (String) catalogLoadNode.getUserData("item_#" + c + "_Metallic_Value");       
                    String advancedRoughnessValueString = (String) catalogLoadNode.getUserData("item_#" + c + "_Advanced_Roughness_Value");
                    String advancedMetallicValueString = (String) catalogLoadNode.getUserData("item_#" + c + "_Advanced_Metallic_Value");      
                    String afflictionModeValueString = (String) catalogLoadNode.getUserData("item_#" + c + "_AfflictionMode_Value");


                    float scaleValue = Float.parseFloat(scaleValueString);
                    float roughnessValue = Float.parseFloat(roughnessValueString);
                    float metallicValue = Float.parseFloat(metallicValueString);                        
                    float advancedRoughnessValue = Float.parseFloat(advancedRoughnessValueString);
                    float advancedMetallicValue = Float.parseFloat(advancedMetallicValueString);
                    int afflictionModeValue = (int) Float.parseFloat(afflictionModeValueString);


                     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    float screenWidth = (float) screenSize.getWidth() * .99f;        
                    float screenHeight = (float) screenSize.getHeight()  * .925f;


                    TerrainTextureSlot loadedSlot = new TerrainTextureSlot(textureSlotName, catalogManager.getTextureSlotManager(), screenWidth, screenHeight);  
                    loadedSlot.setDisplayName(itemDisplayName);
//                            
//                        loadedSlot.setAlbedoTexture(attemptToLoadTexture(albedoMapAssetKey));
//                        loadedSlot.setNormalMapTexture(attemptToLoadTexture(normalMapAssetKey));
//                        loadedSlot.setParallaxTexture(attemptToLoadTexture(parallaxMapAssetKey));
//                        loadedSlot.setPackedNormalParallaxTexture(attemptToLoadTexture(packedNormalParallaxMapAssetKey));
//                        loadedSlot.setRoughnessTexture(attemptToLoadTexture(roughnessMapAssetKey));
//                        loadedSlot.setMetallicTexture(attemptToLoadTexture(metallicMapAssetKey));
//                        loadedSlot.setAmbientOcclusionTexture(attemptToLoadTexture(aoMapAssetKey));
//                        loadedSlot.setEmissiveIntensityTexture(attemptToLoadTexture(eiMapAssetKey));
//                        loadedSlot.setPackedMetallicRoughnessAoEiTexture(attemptToLoadTexture(packedMetallicRoughnessAoEiMapAssetKey));
//
//                                                              

                    loadedSlot.setAlbedoAssetKey(albedoMapAssetKey);
                    loadedSlot.setNormalMapAssetKey(normalMapAssetKey);
                    loadedSlot.setParallaxAssetKey(parallaxMapAssetKey);
                    loadedSlot.setPackedNormalParallaxAssetKey(packedNormalParallaxMapAssetKey);
                    loadedSlot.setRoughnessAssetKey(roughnessMapAssetKey);
                    loadedSlot.setMetallicAssetKey(metallicMapAssetKey);
                    loadedSlot.setAoAssetKey(aoMapAssetKey);
                    loadedSlot.setEiAssetKey(eiMapAssetKey);
                    loadedSlot.setPackedMetallicRoughnessAoEiAssetKey(packedMetallicRoughnessAoEiMapAssetKey);

                    loadedSlot.setScale(scaleValue);
                    loadedSlot.setRoughnessValue(roughnessValue);
                    loadedSlot.setMetallicValue(metallicValue);
                    loadedSlot.setAdvancedMetallicValue(advancedMetallicValue);
                    loadedSlot.setAdvancedRoughnessValue(advancedRoughnessValue);
                    loadedSlot.setAfflictionMode(afflictionModeValue);

                    loadedSlot.setAlbedoTexArrayId(albedoTextArrayId);
                    loadedSlot.setPackedNormalParallaxTexArrayId(packedNormalParallaxTextArrayId);
                    loadedSlot.setPackedMetallicRoughnessAoEitextureId(packedMetallicRoughnessAoEiTextArrayId);


                    TerrainTextureSlotCatalogItem loadedItem = new TerrainTextureSlotCatalogItem(loadedSlot, this);

                    if(tagsString != null){
                          loadedItem.fillTagsFromSplitString(tagsString, catalogManager);
                    }


                    loadedItem.setName(textureSlotName);
                    loadedItem.setDisplayName(itemDisplayName);


                    catalogItems.add(loadedItem);



                    // - - - -









             }

        }


              
    }
    
   

   
    
    
}
