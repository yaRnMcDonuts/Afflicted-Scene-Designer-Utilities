/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog;

import java.util.ArrayList;

/**
 *
 * @author ryan
 */

public class CatalogItem {
    
    private Catalog catalogContainingThisItem;
      
    public String assetName;
    public String getName(){ return assetName; }
    public void setName(String name){ assetName = name; }
    public String getDisplayName(){ return assetName; } 
    
    private String assetKeyString;
    public String getAssetKeyString(){ return assetKeyString; }
    
    //a list of strings that reference an alternate material with the same name as the originalMaterial, with the materialVariant string added on afterwards. 
    // this way you dont need 5 copies of the same model to reference 5 version with unique materials
    
    private ArrayList<String> materialVariants = new ArrayList();
    public ArrayList<String> getMaterialVariants(){ return materialVariants; }
    
    private ArrayList<AssetTag> categoryTags = new ArrayList();
    public ArrayList<AssetTag> getAssetTagsList() { return categoryTags;  }
    
    private ArrayList<AssetTag> primaryCategoryTags = new ArrayList();
    public ArrayList<AssetTag> getPrimaryCategoryTag() { return primaryCategoryTags;  }
    
    
    private String selectedMaterialVariant = "";
    public String getSelectedMaterialVariant(){ return selectedMaterialVariant; }
    public void setMaterialVariant(String materialVariantString){
        if(materialVariants.contains(materialVariantString) || materialVariantString.equals("")){
            selectedMaterialVariant = materialVariantString;
        }
    }
  
    
    
    
    public CatalogItem(String name, String key, Catalog catalog){
        assetName = name;
        assetKeyString = key;
        catalogContainingThisItem = catalog;
    }
    
    
    

    public void addMaterialVariant(String materialVariantExtensionString){
        materialVariants.add(materialVariantExtensionString);
    }
    
  //methods for adding and removing tags to a model. important that these are stored and saved/loaded properly    
    public void addTag(AssetTag tag){  
        categoryTags.add(tag); 
        
        checkForPrimaryTag(tag);
    
    }
    public void removeTag(AssetTag tag){
        if(categoryTags.contains(tag)){
            categoryTags.remove(tag);
        } 
    }
    
    public boolean hasTag(AssetTag tag){
        if(categoryTags.contains(tag)){
             return true;
        }else{
            return false;
        }        
    } 
    
     public String getAllMaterialVariantsAsString() {
        String materialVariantsString = "";
        
        for(int t = 0; t < materialVariants.size(); t++){
            String naterialVariantName = materialVariants.get(t);
            
            if(t == 0){
                materialVariantsString = naterialVariantName;
            }else{
                materialVariantsString += "," + naterialVariantName;
            }
            
            
        }
            
        return materialVariantsString;
          
    }

    public String getAllTagsAsString() {
        String tagsString = "";
        
        for(int t = 0; t < categoryTags.size(); t++){
            AssetTag tag = categoryTags.get(t);
            String tagName = tag.getString();
            
            if(t == 0){
                tagsString = tagName;
            }else{
                tagsString += "," + tagName;
            }
            
            
        }
            
        return tagsString;
          
    }
    
     public void fillMaterialVariantsFromSplitString(String savedStringToSplit, CatalogManager catalogManager) {
        if(savedStringToSplit != null){
            String[] materialVariantStrings = savedStringToSplit.split(",");
            
            
            for(int c = 0; c < materialVariantStrings.length; c ++){
                materialVariants.add(materialVariantStrings[c]);
                
            }
        }
      
         
    }
    

    public void fillTagsFromSplitString(String savedStringToSplit, CatalogManager catalogManager){
        
        if(savedStringToSplit != null){
            String[] tagStrings = savedStringToSplit.split(",");
            categoryTags = catalogContainingThisItem.getValidTagsForItem(tagStrings);
            
            for(int c = 0; c < categoryTags.size(); c ++){
                AssetTag tag = categoryTags.get(c);
                checkForPrimaryTag(tag);
            }
        }
        
        
    }

    private void checkForPrimaryTag(AssetTag tag) {
    
        String tagString = tag.getString();
        if(tagString.equalsIgnoreCase("tree") || tagString.equalsIgnoreCase("rock") || tagString.equalsIgnoreCase("architecture") || tagString.equalsIgnoreCase("foilage") || tagString.equalsIgnoreCase("quest")){
            primaryCategoryTags.add(tag);
        }
    }

    public void clearTags() { 
        categoryTags.clear();
        primaryCategoryTags.clear();
        
    }

    public void setAssetPath(String pathString){ assetKeyString = pathString;  }

    public void clearMatVars() {
        materialVariants.clear();
    }



   

}
