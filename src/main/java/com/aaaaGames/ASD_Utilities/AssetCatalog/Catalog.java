/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog;

import ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog.TerrainTextureSlot;
import ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog.TerrainTextureSlotCatalogItem;
import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryan
 */
public class Catalog {
    
    public SimpleApplication app;
    
    public final CatalogManager catalogManager;
    
    public CatalogManager getCatalogManager() { return catalogManager; }
    
    public final ArrayList <CatalogItem> catalogItems = new ArrayList();
    public ArrayList<CatalogItem> getCatalogItems() { return catalogItems;  }
    
    public AddCatalogItemTool addCatalogItemTool;
    public AddCatalogItemTool getAddCatalogItemTool(){ 
        if(addCatalogItemTool == null){
            addCatalogItemTool = new AddCatalogItemTool(catalogManager);
        } 
        return addCatalogItemTool; }
    
    public final String catalogFilePath;
    
    public final String catalogName;
    public String getCatalogName() { return catalogName; }
    
    public final String catalogFilName;
    
    
    public Catalog(CatalogManager cm, String name){
        
        catalogManager = cm;
        
        app = catalogManager.getApp();
        
        catalogName = name + " Catalog";
        catalogFilName = name + "_Catalog";
    
        //load json file for catalogFilePath
        
        catalogFilePath = "AssetCatalogs/" + name + "_Catalog.json";

         
   //     app.enqueue(() ->{

            loadCatalogFromNode();
        
    //    });
        
        
    }
    
    
    public ArrayList<AssetTag> registeredTags = new ArrayList();
    public HashMap<String, AssetTag> tagsMap= new HashMap(); //NOTE: all tag names are put to lowercase, so the key Strings in this map should always be all lowercase
    
    public ArrayList getRegisteredTagsList(){ return registeredTags;  }
    public HashMap getRegisteredTagsMap(){ return tagsMap; } 
    public void unregisterTag(AssetTag tag) {    
        tagsMap.remove(tag.getString());
        registeredTags.remove(tag);
    }
    
    
    
     //returns null if tag already exists
    public AssetTag registerNewTag(String tagName){
        tagName = tagName.toLowerCase();//force all tag names to lower case.
        
        if(!tagsMap.containsKey(tagName)){
            AssetTag newTag = new AssetTag(tagName); 
        
            registeredTags.add(newTag);
            tagsMap.put(tagName, newTag);

            return newTag;
        }else{
            return null;
        }
       
    }
    
    //returns null if tag doesnt exist 
    public AssetTag getTag(String tagName){
        tagName = tagName.toLowerCase();
        
        if(tagsMap.containsKey(tagName)){
            AssetTag assetTag = tagsMap.get(tagName);
            return assetTag;
        }else{
            return null;
        }
    }
    
    public ArrayList getValidTagsForItem(String[] tagStrings){
        ArrayList<AssetTag> tagsForItem = new ArrayList();
        
        for(int t = 0; t < tagStrings.length; t++){
            String tagString = tagStrings[t];
            
            
            AssetTag assetTag = getTag(tagString);
            if(assetTag == null){
                assetTag = registerNewTag(tagString);
            }
            
            tagsForItem.add(assetTag);            
        }
        
        return tagsForItem;
        
    }
    
    
    public void addNewCatalogItem(CatalogItem item) {    catalogItems.add(item);  }
    public void removeCatalogItem(CatalogItem catalogItemToRemove) { catalogItems.remove(catalogItemToRemove);    }
    
    
    public void saveCatalogItems(){

        //save these to a j3o file in the 
        
        Node catalogSaveNode = new Node("Model_Catalog_SaveNode");
        
        int totalItemCount = catalogItems.size();

        catalogSaveNode.setUserData("CatalogName", catalogName);
        catalogSaveNode.setUserData("TotalItemCount", totalItemCount);


        for(int c = 0; c < catalogItems.size(); c++){
            CatalogItem item = catalogItems.get(c);

            String itemName = item.getName();
            String itemAssetKey = item.getAssetKeyString();
            String tagsString = item.getAllTagsAsString();
            String matVarsString = item.getAllMaterialVariantsAsString();

            catalogSaveNode.setUserData("item_#" + c + "_Name", itemName);
            catalogSaveNode.setUserData("item_#" + c + "_AssetKey", itemAssetKey);
            catalogSaveNode.setUserData("item_#" + c + "_Tags", tagsString);
            catalogSaveNode.setUserData("item_#" + c + "_MatVars", matVarsString);

        }

       saveFileToNode(catalogSaveNode);


    
    }
    
    public void saveFileToNode(Node catalogSaveNode){
              BinaryExporter exporter = BinaryExporter.getInstance();
        
    //    File file = new File(userHome + "/AFDSavedMaps/" + activeMapName + "/" + nodeToSave.getCatalogName() + ".j3o");
    
        File file = new File(catalogManager.getAssetDirectory() + "/Catalogs/" + catalogFilName + ".j3o");
        
        try {            
            exporter.save(catalogSaveNode, file);          
          
        } catch (IOException ex) {
            System.out.println("Error: Failed to save Catalog:  " + getCatalogName());
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error: Failed to save Catalog:  " + getCatalogName(), ex);
        }
        
    }
    
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
                 String itemName, itemAssetKey, tagsString, materialVariantsString;
                 
                 itemName = (String) catalogLoadNode.getUserData("item_#" + c + "_Name");
                 itemAssetKey = (String) catalogLoadNode.getUserData("item_#" + c + "_AssetKey");
                 tagsString = (String) catalogLoadNode.getUserData("item_#" + c + "_Tags");
                 materialVariantsString = (String) catalogLoadNode.getUserData("item_#" + c + "_MatVars");
                 
                 CatalogItem loadedItem = new CatalogItem(itemName, itemAssetKey, this);
                 
                  if(tagsString != null){
                    loadedItem.fillTagsFromSplitString(tagsString, catalogManager);
                  }
                  
                 if(materialVariantsString != null){
                     loadedItem.fillMaterialVariantsFromSplitString(materialVariantsString, catalogManager);
                 }
                                  
                 catalogItems.add(loadedItem);
                 
             }
            
        }       
          
    }
    
       
     public CatalogItem findCatalogItem(String searchName) {
        for(int i = 0; i < catalogItems.size(); i++){
            
            TerrainTextureSlotCatalogItem existingItem = (TerrainTextureSlotCatalogItem) catalogItems.get(i);
            TerrainTextureSlot existingSlot = existingItem.getTerrainTextureSlot();
            
            if(existingItem.getName().equals(searchName)|| existingSlot.getName().equals(searchName)){
                return existingItem;
            }
        }
        return null;
        
    }
     
     
    
     
   

 
    
        

    
}
