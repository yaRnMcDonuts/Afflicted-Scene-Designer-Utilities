/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog;

import ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter.TerrainShaderConverter;
import ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter.TerrainShaderConverter.TerrainShaderMode;
import ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter.TextureArrayManager;
import ASD_Utilities.AssetCatalog.CatalogItem;
import ASD_Utilities.AssetCatalog.CatalogManager;
import ASD_Utilities.TextureUtilities.TextureChannelPacker;
import ASD_Utilities.TextureUtilities.TextureChannelPacker.TextureChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import com.jme3.texture.image.ColorSpace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ryan
 */

public class TextureSlotManager {    
       
    public SimpleApplication app;
    public SimpleApplication getApp() { return app;  }

        
    private boolean showAfflictionModes;
    
    
        
        
    public ArrayList<TerrainTextureSlot> registeredTextureSlots = new ArrayList();
    private HashMap<String, TerrainTextureSlot> registeredTextureSlotsMap = new HashMap();
    
    public TerrainShaderConverter terrainShaderConverter;
    public  TerrainShaderConverter getTerrainShaderConverter(){ return terrainShaderConverter ; }
    
    private float screenWidth, screenHeight;
    
    private TextureChannelPacker texturePacker;
    public TextureChannelPacker getTexturePacker(){ return texturePacker; }
    
    
    private TextureArrayManager textureArrayManager;
    public TextureArrayManager getTextureArrayManager() { return textureArrayManager; }
    
    public CatalogManager catalogManager;
    
    public TextureSlotManager(CatalogManager catalogManager, float screenWidth, float screenHeight){
         this.catalogManager = catalogManager;
         
         app = catalogManager.getApp();
        
         
        textureArrayManager = catalogManager.getASDUCore().getTextureArrayManager();
        terrainShaderConverter = catalogManager.getASDUCore().getTerrainShaderConverter();        
        texturePacker = catalogManager.getASDUCore().getTextureChannelPacker();
                
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        showAfflictionModes = true;      
        
        
        
        
        
    }
    
    public void fillTextureSlotsFromCatalog(){
        TerrainTextureSlotCatalog textureSlotCatalog = catalogManager.getTerrainTextureSlotCatalog();
        ArrayList<CatalogItem> catalogItems = textureSlotCatalog.getCatalogItems();
        
        for(int c = 0; c < catalogItems.size(); c++){
            TerrainTextureSlotCatalogItem catalogItem = (TerrainTextureSlotCatalogItem) catalogItems.get(c);
            TerrainTextureSlot slot = catalogItem.getTerrainTextureSlot();
            
            
            this.registerNewTextureSlot(slot.getName(), slot);
        }
    }
    
    public void update(float tpf){
        for(int i = 0; i < registeredTextureSlots.size();i++){
            
            TerrainTextureSlot terrainTextureSlot = registeredTextureSlots.get(i);
            terrainTextureSlot.update(tpf);
            
        }
    }
    
    public void registerTextureSlotsForTerrain(Terrain terrain) {
        
        Material terrainMat = terrain.getMaterial();
        String terrainMatDef = terrainMat.getMaterialDef().getAssetName();
        
        
        for(int i = 0; i < 12; i++){
            
            
            TerrainTextureSlot textureSlot = null;
            
            
            
            
            String udKeyString = "TexSlot_" + i;
            
            String textureSlotName = ((Spatial)terrain).getUserData(udKeyString);
            
            if(textureSlotName != null){
                 textureSlot = registeredTextureSlotsMap.get(textureSlotName);
                 
            }   
            
            
            
            if(textureSlot == null){
                
                
                if(terrainShaderConverter.isPhongMat(terrainMatDef)){
                    textureSlot = makeNewSlotFromPhong((TerrainQuad) terrain, terrainMat, i, textureSlotName);
                    
                }
                else if(terrainShaderConverter.isPbrMat(terrainMatDef)){
                    
                    textureSlot = makeNewSlotFromPbr((TerrainQuad) terrain, terrainMat, i, textureSlotName);
                    
                 

                }
                else if(terrainShaderConverter.isAdvancedPbrMat(terrainMatDef)){
                    
                    //an advanced pbr terrain without user data tex slot names (aka one that was made out of this editor) cant be reverted to the more basic shaders! as the original asset path references are lost without the user data names to find and pair them
                    //painting and editing is still possible, but the possibility to revert to phong or normal pbr from 'pbr+' will need disabled in the ManageTerrainTool... or will it matter since normal map and albedo are the only 2
                    // that can be used by normal pbr anyways, and the packedNormalParallax will suffice. although the metallic/roughness  values would still be set to 1.0 since theyd be expecting a texture map to multiply by
                    
                    
                    textureSlot = makeNewSlotFromAdvancedPbr((TerrainQuad) terrain, terrainMat, i, textureSlotName);
                    
                }
                
                
                

                 
                 
                 
                 
            
            }
                 if(textureSlot != null){
                    textureSlot = registerNewTextureSlot(textureSlot.getName(), textureSlot);                   
                    
                    
                    textureSlot.setShaderMode(terrainShaderConverter.getShaderModeOfTerrain(terrain));
                    textureSlot.registerTerrainInSlot(terrain, i);
                    
                    //calling this will forcibly refresh all terrains to match the catalog item correlating with this slot (regardless of the params the terrain was last svaed with. avoid changing registered slots
                    textureSlot.mapSlotToShader(terrain, textureSlot.getSlotIdForTerrain(terrain));                            // from other editors to avoid this, I.E: you can paint in other editors, but dont change textures or scale in other editors
                    
                    
                }                   
            
        }
    
    }
    
    public TerrainTextureSlot registerNewTextureSlot(String newSlotName, TerrainTextureSlot textureSlot){
        
        TerrainTextureSlot registeredSlot = registeredTextureSlotsMap.get(newSlotName);
        
        if(registeredSlot == null){
            registeredTextureSlotsMap.put(newSlotName, textureSlot); //add the newly created slot that had a non-existent name
            registeredTextureSlots.add(textureSlot);
            
            //attempt to register texture slots to catalog on load (attempting to add an item that already exists will just return the already existing item, which can then be used instead)
            TerrainTextureSlotCatalogItem validItem = catalogManager.getTerrainTextureSlotCatalog().addNewTerrainTextureSlotCatalogItem(textureSlot);
            textureSlot = validItem.getTerrainTextureSlot();
            
            return textureSlot;
                
        }
        else{
            
            return registeredSlot; //returns the existing slot if the name is already taken for this slot
        }
    }
    
    private TerrainTextureSlot makeNewSlotFromPhong(TerrainQuad terrain, Material terrainMat, int i, String slotName) {
    
            
        TerrainTextureSlot textureSlot = null;
        String albedoMapName = "DiffuseMap";
        
        if(i > 0){
            albedoMapName += "_" + i;
        }
        
        

        MatParamTexture albedoMapMatParam = terrainMat.getTextureParam(albedoMapName);
        if(albedoMapMatParam != null){
            Texture albedoTexture = albedoMapMatParam.getTextureValue();

            if(slotName == null){
                String udKeyString = "TexSlot_" + i;
                
               slotName = albedoTexture.getName() + "_Generated_Slot";
               terrain.setUserData(udKeyString, slotName);
            }
            
            //check if the slot already exists before creating it
            textureSlot = registeredTextureSlotsMap.get(slotName);
            
            if(textureSlot == null){
                textureSlot = new TerrainTextureSlot(slotName, this, screenWidth, screenHeight);

                textureSlot.setAlbedoTexture(albedoTexture);


                String normalMapString = "NormalMap";
                if(i > 0){
                     normalMapString += "_" + i;
                }

                MatParamTexture normalMapMatParam = terrainMat.getTextureParam(normalMapString);  //may have a parallax packed in alpha channel. if so, it will be noted in the textureSlots save file
                if(normalMapMatParam != null){
                    Texture normalMapTexture = normalMapMatParam.getTextureValue();
                    textureSlot.setNormalMapTexture(normalMapTexture);

                }

                float scale = 1f;
                MatParam scaleMatParam = terrainMat.getParam("DiffuseMap_" + i + "_scale");
                if(scaleMatParam != null){
                    scale = (float) scaleMatParam.getValue();
                }
                
                scale = getScaleAdjustedForTriPlanar(terrainMat, scale);
                textureSlot.setScale(scale);

            
                if(showAfflictionModes){
                 //   Vector3f afflictionMode = new Vector3f(0,0,0);
                    int afflictionMode = 0;
                    MatParam afflitionModeParam = terrainMat.getParam("AfflictionMode_" + i);
                    if(afflitionModeParam != null){
                        afflictionMode = (int) afflitionModeParam.getValue();
                    }
                    textureSlot.setAfflictionMode(afflictionMode);


                }
            }

            
            
            
            
            
        }
        
        return textureSlot;
    }

    
    public TerrainTextureSlot makeNewSlotFromPbr(TerrainQuad terrain, Material terrainMat, int i, String slotName){
        
        TerrainTextureSlot textureSlot = null;
           String albedoMapName = "AlbedoMap_" + i;

   

        MatParamTexture albedoMapMatParam = terrainMat.getTextureParam(albedoMapName);
        if(albedoMapMatParam != null){
            Texture albedoTexture = albedoMapMatParam.getTextureValue();

            if(slotName == null){
                String udKeyString = "TexSlot_" + i;
                
               slotName = albedoTexture.getName() + " _Generated_Slot";
               terrain.setUserData(udKeyString, slotName);
            }
           

             
            textureSlot = registeredTextureSlotsMap.get(slotName);
            
            
            if(textureSlot == null){
                textureSlot = new TerrainTextureSlot(slotName, this, screenWidth, screenHeight);

                textureSlot.setAlbedoTexture(albedoTexture);




                MatParamTexture normalMapMatParam = terrainMat.getTextureParam("NormalMap_" + i);  //may have a parallax packed in alpha channel. if so, it will be noted in the textureSlots save file
                if(normalMapMatParam != null){
                    Texture normalMapTexture = normalMapMatParam.getTextureValue();
                    textureSlot.setNormalMapTexture(normalMapTexture);

                }

              



                float roughness = 1.0f;
                MatParam roughnessMatParam = terrainMat.getParam("Roughness_" + i);
                if(roughnessMatParam != null){
                    roughness = (float) roughnessMatParam.getValue();
                }
                textureSlot.setRoughnessValue(roughness);

                float metallic = 0.01f;
                MatParam metallicMatParam = terrainMat.getParam("Metallic_" + i);
                if(metallicMatParam != null){
                    metallic = (float) metallicMatParam.getValue();
                }
                textureSlot.setMetallicValue(metallic);


                
                float scale = 1f;
                MatParam scaleMatParam = terrainMat.getParam("AlbedoMap_" + i + "_scale");
                if(scaleMatParam != null){
                    scale = (float) scaleMatParam.getValue();
                }
                
                scale = getScaleAdjustedForTriPlanar(terrainMat, scale);
                textureSlot.setScale(scale);


                if(showAfflictionModes){
                 //   Vector3f afflictionMode = new Vector3f(0,0,0);
                    int afflictionMode = 0;
                    MatParam afflitionModeParam = terrainMat.getParam("AfflictionMode_" + i);
                    if(afflitionModeParam != null){
                        afflictionMode = (int) afflitionModeParam.getValue();
                    }
                    textureSlot.setAfflictionMode(afflictionMode);


                }

                
            }

        }
        
        return textureSlot;
        
    }
   
    
    //get ids and texArrays
    public TerrainTextureSlot makeNewSlotFromAdvancedPbr(TerrainQuad terrain, Material terrainMat, int i, String slotName){
        
        
        
        TerrainTextureSlot textureSlot = null;
           String albedoMapName = "AlbedoMap_" + i;
        MatParam albedoMapMatParam = terrainMat.getTextureParam(albedoMapName);
        
        
        if(albedoMapMatParam != null){
            
        //assign tex arrays that this slots paired to    
            TextureArray albedoTexArray = (TextureArray) terrainMat.getParam("AlbedoTextureArray").getValue();
            TextureArray normalParallaxTexArray = (TextureArray) terrainMat.getParam("NormalParallaxTextureArray").getValue();
            TextureArray metallicRoughnessAoEiTexArray = (TextureArray) terrainMat.getParam("NormalParallaxTextureArray").getValue();                   
            
            
            
            int albedoTextureId = (int) albedoMapMatParam.getValue();      
            //texture array needs recreated based on original texture params. 
            

            if(slotName == null){
                String udKeyString = "TexSlot_" + i;
                
                //a slot generated from advancedPbr that doesn't have a UD name will be named after the albedoTexArray name + the index of the albedo map in the tex array
                slotName = albedoTexArray.getName() + "_Index-" + albedoTextureId + "_Generated_Slot";
                terrain.setUserData(udKeyString, slotName);
            }
           

             
            textureSlot = registeredTextureSlotsMap.get(slotName);
            
            if(textureSlot == null){ //this will only be null if terrains been edited outside of this editor.. otherwise it shouldve been able to find the slot from pre-loaded catalog at line above this
                
                
                textureSlot = new TerrainTextureSlot(slotName, this, screenWidth, screenHeight);

                textureSlot.setAlbedoTexArrayId(albedoTextureId);


                MatParam normalMapMatParam = terrainMat.getTextureParam("NormalMap_" + i);
                if(normalMapMatParam != null){
                    int normalMapTextureId = (int) normalMapMatParam.getValue();
                    textureSlot.setPackedNormalParallaxTexArrayId(normalMapTextureId);

                }
                
                MatParam metallicRoughnessMapMatParam = terrainMat.getTextureParam("MetallicRoughnessMap_" + i);  
                if(metallicRoughnessMapMatParam != null){
                    int metallicRoughnessMapTextureId = (int) metallicRoughnessMapMatParam.getValue();
                    textureSlot.setPackedMetallicRoughnessAoEitextureId(metallicRoughnessMapTextureId);

                }
                
                


                float roughness = 1.0f;
                MatParam roughnessMatParam = terrainMat.getParam("Roughness_" + i);
                if(roughnessMatParam != null){
                    roughness = (float) roughnessMatParam.getValue();
                }
                textureSlot.setRoughnessValue(roughness);

                float metallic = 0.01f;
                MatParam metallicMatParam = terrainMat.getParam("Metallic_" + i);
                if(metallicMatParam != null){
                    metallic = (float) metallicMatParam.getValue();
                }
                textureSlot.setMetallicValue(metallic);



                   
                float scale = 1f;
                MatParam scaleMatParam = terrainMat.getParam("AlbedoMap_" + i + "_scale");
                if(scaleMatParam != null){
                    scale = (float) scaleMatParam.getValue();
                }
                
                scale = getScaleAdjustedForTriPlanar(terrainMat, scale);
                textureSlot.setScale(scale);
                

                if(showAfflictionModes){
                 //   Vector3f afflictionMode = new Vector3f(0,0,0);
                    int afflictionMode = 0;
                    MatParam afflitionModeParam = terrainMat.getParam("AfflictionMode_" + i);
                    if(afflitionModeParam != null){
                        afflictionMode = (int) afflitionModeParam.getValue();
                    }
                    textureSlot.setAfflictionMode(afflictionMode);


                }


            }
            
            
            //put code here to re-generate texture arrays.. but first fix problem where any second,third,forth,etc material using an existant texture array turns transparent.. 
            //also might want/need to reorganize code that creates texture arrays into the TextureArrayManager class more so it can be reused here and in shader converter easiest, once working..
            
            
            textureSlot.setAlbedoTexArray(albedoTexArray);
            textureSlot.setNormalParallaxTexArray(normalParallaxTexArray);
            textureSlot.setMetallicRoughnessAoEiTexArray(metallicRoughnessAoEiTexArray);
            


        }
        
        return textureSlot;
        
    }
    
    
    //load any saved texture slots so they can be reassigned and edited easily
    private void loadTextureSlotsFromFile(){
        
        
    }


    public TerrainTextureSlot getTextureSlot(String slotName) {
        return registeredTextureSlotsMap.get(slotName);
    
    }

 
    
    public TerrainTextureSlot selectedTextureSlot;
    
    public void setSelectedTextureSlot(TerrainTextureSlot textureSlot) {
        
        if(selectedTextureSlot != null){
            selectedTextureSlot.unselect();
        }
        
        selectedTextureSlot = textureSlot;
    }



    public TerrainTextureSlot getSelectedTextureSlot() {  return selectedTextureSlot;}

    int aCount = 0;

    private boolean once = true;
    //swap the position of two texture slots (I.E. their color channel on alphamap_ 0 1 or 2) for a selected terrain
    public void swapSlotPositionsForTextureSlots(TerrainTextureSlot slotA, TerrainTextureSlot slotB, Terrain terrain) throws IOException{
        
        if (slotA.isPairedToTerrain(terrain) && slotB.isPairedToTerrain(terrain)){
            
            Material terrainMat = terrain.getMaterial();
            
            TextureChannel textureChannelA = slotA.getChannelForTerrainInSlot(terrain);
            TextureChannel textureChannelB = slotB.getChannelForTerrainInSlot(terrain);
            
            
            String alphaMapStringA = slotA.getAlphaMapForTerrainInSlot(terrain);
            String alphaMapStringB = slotB.getAlphaMapForTerrainInSlot(terrain);
            
            Texture alphaMapTextureA = terrainMat.getTextureParam(alphaMapStringA).getTextureValue();
            Texture alphaMapTextureB = terrainMat.getTextureParam(alphaMapStringB).getTextureValue();
            
            Image alphaImageA = alphaMapTextureA.getImage().clone();
            Image alphaImageB = alphaMapTextureB.getImage().clone();
            
//            if(once){
//                texturePacker.getOutputPic().setBackground( new QuadBackgroundComponent(new Texture2D(alphaImageB)));
//                 texturePacker.getEmissivePic().setBackground(new QuadBackgroundComponent(new Texture2D(alphaImageA)));
//                once = false;
//                
//                texturePacker.showDebug();
//            }
                
            
            if(alphaMapTextureA.equals(alphaMapTextureB)){
                
                
                Image newAlphaMapImage = terrainShaderConverter.makeBlankAlphaMap(alphaImageA.getHeight(), terrain, 0, "tempAlphaMaps/AlphaMapCopyHolder_" + aCount++ +".png").getImage();
            
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.R, newAlphaMapImage, TextureChannel.R, ColorSpace.Linear);
                
                
                
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.G, newAlphaMapImage, TextureChannel.G, ColorSpace.Linear);
                 
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.B, newAlphaMapImage, TextureChannel.B, ColorSpace.Linear);
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.A, newAlphaMapImage, TextureChannel.A, ColorSpace.Linear);
                
                
                texturePacker.packChannelFromTo(alphaImageB.clone(), textureChannelB, newAlphaMapImage, textureChannelA, ColorSpace.Linear);                
                 texturePacker.packChannelFromTo(alphaImageB.clone(), textureChannelA, newAlphaMapImage, textureChannelB, ColorSpace.Linear);
                 
                 
                
                 
                 
                alphaMapTextureA.setImage(newAlphaMapImage); //only needs to update one alphaMap since the two channels being swapped were on the same alphaMap
                
                terrainMat.setTexture(alphaMapStringA, alphaMapTextureA);
                
                
            }
            else{
                
                Image newAlphaMapImageA = terrainShaderConverter.makeBlankAlphaMap(alphaImageA.getHeight(), terrain, 0, "tempAlphaMaps/AlphaMapCopyHolder_" + aCount++ +".png").getImage();
                Image newAlphaMapImageB = terrainShaderConverter.makeBlankAlphaMap(alphaImageB.getHeight(), terrain, 0, "tempAlphaMaps/AlphaMapCopyHolder_" + aCount++ +".png").getImage();
                
                
                texturePacker.packChannelFromTo(alphaImageA, TextureChannel.R, newAlphaMapImageA, TextureChannel.R, ColorSpace.Linear);
                texturePacker.packChannelFromTo(alphaImageA, TextureChannel.G, newAlphaMapImageA, TextureChannel.G, ColorSpace.Linear);
                texturePacker.packChannelFromTo(alphaImageA, TextureChannel.B, newAlphaMapImageA, TextureChannel.B, ColorSpace.Linear);
                texturePacker.packChannelFromTo(alphaImageA, TextureChannel.A, newAlphaMapImageA, TextureChannel.A, ColorSpace.Linear);
                
                
                
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.R, newAlphaMapImageB, TextureChannel.R, ColorSpace.Linear);                        
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.G, newAlphaMapImageB, TextureChannel.G, ColorSpace.Linear);                 
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.B, newAlphaMapImageB, TextureChannel.B, ColorSpace.Linear);
                texturePacker.packChannelFromTo(alphaImageB, TextureChannel.A, newAlphaMapImageB, TextureChannel.A, ColorSpace.Linear);
                
                
               
//                
               texturePacker.packChannelFromTo(alphaImageB, textureChannelB, newAlphaMapImageA, textureChannelA, ColorSpace.Linear);              
               texturePacker.packChannelFromTo(alphaImageA, textureChannelA, newAlphaMapImageB, textureChannelB, ColorSpace.Linear);
//                           
//               
               
               
                alphaMapTextureA.setImage(newAlphaMapImageA);
                alphaMapTextureB.setImage(newAlphaMapImageB);
                
                terrainMat.setTexture(alphaMapStringA, alphaMapTextureA);
                terrainMat.setTexture(alphaMapStringB, alphaMapTextureB);
                
                
            }
           
      
            
         //swap texture map indexes between two slots for Phong / PBR / AdvancedPBR shaders accordingly
         
    //PHONG
            if(slotA.getShaderMode().equals(TerrainShaderMode.PHONG)){
                
                System.out.println(" SWAPPPP");
                 
                int slotIndexA = slotA.getSlotIdForTerrain(terrain);
                int slotIndexB = slotB.getSlotIdForTerrain(terrain);
                
                String diffuseSlotIndexAString = "DiffuseMap";
                String diffuseSlotIndexBString = "DiffuseMap";
                
                if(slotIndexA > 0){
                    diffuseSlotIndexAString += "_" + slotIndexA ;
                }
                if(slotIndexB > 0){
                    diffuseSlotIndexBString += "_" + slotIndexB ;
                }
                
                Texture diffuseMapA = null, diffuseMapB = null;
                
                MatParamTexture diffuseMapMatParamA = terrainMat.getTextureParam(diffuseSlotIndexAString);
                if(diffuseMapMatParamA != null){
                    diffuseMapA = diffuseMapMatParamA.getTextureValue();
                }
                MatParamTexture diffuseMapMatParamB = terrainMat.getTextureParam(diffuseSlotIndexBString);
                if(diffuseMapMatParamB != null){
                    diffuseMapB = diffuseMapMatParamB.getTextureValue();
                }
                
                if(diffuseMapA != null){
                    terrainMat.setTexture(diffuseSlotIndexBString, diffuseMapA);
                    if(diffuseMapB == null){
                        terrainMat.clearParam(diffuseSlotIndexAString); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(diffuseMapB != null){
                    terrainMat.setTexture(diffuseSlotIndexAString, diffuseMapB);
                    if(diffuseMapA == null){
                        terrainMat.clearParam(diffuseSlotIndexBString); //clear slot B if slot A was not defined / null
                    }
                }
            //swap scales     

                MatParam scaleAParam = terrainMat.getParam("DiffuseMap_" + slotIndexA + "_scale"); 
                float scaleA = 1; 
                if(scaleAParam != null){
                    scaleA = (float) scaleAParam.getValue();
                }
                
                MatParam scaleBParam = terrainMat.getParam("DiffuseMap_" + slotIndexB + "_scale"); 
                float scaleB = 1; 
                if(scaleBParam != null){
                 scaleB = (float) scaleBParam.getValue();
                }

                terrainMat.setFloat("DiffuseMap_" + slotIndexA + "_scale", scaleB);
                terrainMat.setFloat("DiffuseMap_" + slotIndexB + "_scale", scaleA);
                
            //swap normal maps if either slot contains one
                Texture normalMapA = null, normalMapB = null;
                
                MatParamTexture normalMapMatParamA = terrainMat.getTextureParam("NormalMap_" + slotIndexA);
                if(normalMapMatParamA != null){
                    normalMapA = normalMapMatParamA.getTextureValue();
                }
                MatParamTexture normalMapMatParamB = terrainMat.getTextureParam("NormalMap_" + slotIndexB);
                if(normalMapMatParamB != null){
                    normalMapB = normalMapMatParamB.getTextureValue();
                }
                
                if(normalMapA != null){
                    terrainMat.setTexture("NormalMap_" + slotIndexB, normalMapA);
                    
                    if(normalMapB == null){
                        terrainMat.clearParam("NormalMap_" + slotIndexA); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(normalMapB != null){
                    terrainMat.setTexture("NormalMap_" + slotIndexA, normalMapB);
                    if(normalMapA == null){
                        terrainMat.clearParam("NormalMap_" + slotIndexB); //clear slot B if slot A was not defined / null
                    }
                }
                
                
                
                
                
            } 
            
    //PBR        
            else if(slotA.getShaderMode().equals(TerrainShaderMode.PBR)){
                
                int slotIndexA = slotA.getSlotIdForTerrain(terrain);
                int slotIndexB = slotB.getSlotIdForTerrain(terrain);
                
                
                Texture albedoMapA = null, albedoMapB = null;
                
                MatParamTexture albedoMapMatParamA = terrainMat.getTextureParam("AlbedoMap_" + slotIndexA);
                if(albedoMapMatParamA != null){
                    albedoMapA = albedoMapMatParamA.getTextureValue();
                }
                MatParamTexture albedoMapMatParamB = terrainMat.getTextureParam("AlbedoMap_" + slotIndexB);
                if(albedoMapMatParamB != null){
                    albedoMapB = albedoMapMatParamB.getTextureValue();
                }
                
                if(albedoMapA != null){
                    terrainMat.setTexture("AlbedoMap_" + slotIndexB, albedoMapA);
                    
                    if(albedoMapB == null){
                        terrainMat.clearParam("AlbedoMap_" + slotIndexA); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(albedoMapB != null){
                    terrainMat.setTexture("AlbedoMap_" + slotIndexA, albedoMapB);
                    if(albedoMapA == null){
                        terrainMat.clearParam("AlbedoMap_" + slotIndexB); //clear slot B if slot A was not defined / null
                    }
                }
                

            //swap scales     
                MatParam scaleAParam = terrainMat.getParam("AlbedoMap_" + slotIndexA + "_scale");
                MatParam scaleBParam = terrainMat.getParam("AlbedoMap_" + slotIndexB + "_scale");
                
                float scaleA = 1; 
                if(scaleAParam != null){
                    scaleA =(float) scaleAParam.getValue();
                }
                float scaleB = 1; 
                if(scaleBParam != null){
                 scaleB = (float) scaleBParam.getValue();
                }

                terrainMat.setFloat("AlbedoMap_" + slotIndexA + "_scale", scaleB);
                terrainMat.setFloat("AlbedoMap_" + slotIndexB + "_scale", scaleA);

           //swap roughness and metallic value

                float roughnessA = (float) slotA.getRoughnessValue();
                float roughnessB = (float) slotB.getRoughnessValue();

                terrainMat.setFloat("Roughness_" + slotIndexA , roughnessB);
                terrainMat.setFloat("Roughness_" + slotIndexB, roughnessA);

                
                
                
                float metallicA = (float) slotA.getMetallicValue();
                float metallicB = (float) slotB.getMetallicValue();

                terrainMat.setFloat("Metallic_" + slotIndexA , metallicB);
                terrainMat.setFloat("Metallic_" + slotIndexB, metallicA);
           
           
                
                
            //swap normal maps if either slot contains one
                Texture normalMapA = null, normalMapB = null;
                
                MatParamTexture normalMapMatParamA = terrainMat.getTextureParam("NormalMap_" + slotIndexA);
                if(normalMapMatParamA != null){
                    normalMapA = normalMapMatParamA.getTextureValue();
                }
                MatParamTexture normalMapMatParamB = terrainMat.getTextureParam("NormalMap_" + slotIndexB);
                if(normalMapMatParamB != null){
                    normalMapB = normalMapMatParamB.getTextureValue();
                }
                
                if(normalMapA != null){
                    terrainMat.setTexture("NormalMap_" + slotIndexB, normalMapA);
                    
                    if(normalMapB == null){
                        terrainMat.clearParam("NormalMap_" + slotIndexA); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(normalMapB != null){
                    terrainMat.setTexture("NormalMap_" + slotIndexA, normalMapB);
                    if(normalMapA == null){
                        terrainMat.clearParam("NormalMap_" + slotIndexB); //clear slot B if slot A was not defined / null
                    }
                }
                
                
            }
            
    //Advanced PBR            
            else if(slotA.getShaderMode().equals(TerrainShaderMode.ADVANCED_PBR)){
                                
                int slotIndexA = slotA.getSlotIdForTerrain(terrain);
                int slotIndexB = slotB.getSlotIdForTerrain(terrain);
                
                
                int albedoMapA = -1, albedoMapB = -1;
                
                System.out.println(slotIndexA +" ... " +  slotIndexB);
                
                MatParam albedoMapMatParamA = terrainMat.getParam("AlbedoMap_" + slotIndexA);
                if(albedoMapMatParamA != null){
                    
                    System.out.println(" 000  SWAPP");
                    albedoMapA = (int) albedoMapMatParamA.getValue();
                }
                MatParam albedoMapMatParamB = terrainMat.getParam("AlbedoMap_" + slotIndexB);
                if(albedoMapMatParamB != null){
                    
                    System.out.println(" 111  SWAPP");
                    albedoMapB = (int) albedoMapMatParamB.getValue();
                }
                
                if(albedoMapA != -1){
                    terrainMat.setInt("AlbedoMap_" + slotIndexB, albedoMapA);
                    System.out.println("SWAPP");
                    if(albedoMapB == -1){
                        terrainMat.clearParam("AlbedoMap_" + slotIndexA); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(albedoMapB != -1){
                    terrainMat.setInt("AlbedoMap_" + slotIndexA, albedoMapB);
                    
                    System.out.println("SWAPP");
                    if(albedoMapA == -1){
                        terrainMat.clearParam("AlbedoMap_" + slotIndexB); //clear slot B if slot A was not defined / null
                    }
                }
                

            //swap scales     
                MatParam scaleAParam = terrainMat.getParam("AlbedoMap_" + slotIndexA + "_scale");
                MatParam scaleBParam = terrainMat.getParam("AlbedoMap_" + slotIndexB + "_scale");
                
                float scaleA = (float) scaleAParam.getValue();
                float scaleB = (float) scaleBParam.getValue();

                terrainMat.setFloat("AlbedoMap_" + slotIndexA + "_scale", scaleB);
                terrainMat.setFloat("AlbedoMap_" + slotIndexB + "_scale", scaleA);

                float roughnessA = (float) slotA.getRoughnessValue();
                float roughnessB = (float) slotB.getRoughnessValue();

                terrainMat.setFloat("Roughness_" + slotIndexA , roughnessB);
                terrainMat.setFloat("Roughness_" + slotIndexB, roughnessA);

                
                float metallicA = (float) slotA.getMetallicValue();
                float metallicB = (float) slotB.getMetallicValue();

                terrainMat.setFloat("Metallic_" + slotIndexA , metallicB);
                terrainMat.setFloat("Metallic_" + slotIndexB, metallicA);
            
         
            //swap normal/parallax maps if either slot contains one
                int normalMapA = -1, normalMapB = -1;
                
                MatParam normalMapMatParamA = terrainMat.getParam("NormalMap_" + slotIndexA);
                if(normalMapMatParamA != null){
                    normalMapA = (int) normalMapMatParamA.getValue();
                }
                MatParam normalMapMatParamB = terrainMat.getParam("NormalMap_" + slotIndexB);
                if(normalMapMatParamB != null){
                    normalMapB = (int) normalMapMatParamB.getValue();
                }
                
                if(normalMapA != -1){
                    terrainMat.setInt("NormalMap_" + slotIndexB, normalMapA);
                    
                    if(normalMapB == -1){
                        terrainMat.clearParam("NormalMap_" + slotIndexA); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(normalMapB != -1){
                    terrainMat.setInt("NormalMap_" + slotIndexA, normalMapB);
                    if(normalMapA == -1){
                        terrainMat.clearParam("NormalMap_" + slotIndexB); //clear slot B if slot A was not defined / null
                    }
                }
                
                       
            //swap met/rough/ao/ei maps if either slot contains one
                int metallicRoughnessMapA = -1, metallicRoughnessMapB = -1;
                
                MatParam metallicRoughnessMapMatParamA = terrainMat.getParam("MetallicRoughnessMap_" + slotIndexA);
                if(metallicRoughnessMapMatParamA != null){
                    metallicRoughnessMapA = (int) metallicRoughnessMapMatParamA.getValue();
                }
                MatParam metallicRoughnessMapMatParamB = terrainMat.getParam("MetallicRoughnessMap_" + slotIndexB);
                if(metallicRoughnessMapMatParamB != null){
                    metallicRoughnessMapB = (int) metallicRoughnessMapMatParamB.getValue();
                }
                
                if(metallicRoughnessMapA != -1){
                    terrainMat.setInt("MetallicRoughnessMap_" + slotIndexB, metallicRoughnessMapA);
                    
                    if(metallicRoughnessMapB == -1){
                        terrainMat.clearParam("MetallicRoughnessMap_" + slotIndexA); //clear slot A if slot B was not defined / null
                    }
                }
                
                if(metallicRoughnessMapB != -1){
                    terrainMat.setInt("MetallicRoughnessMap_" + slotIndexA, metallicRoughnessMapB);
                    if(metallicRoughnessMapA == -1){
                        terrainMat.clearParam("MetallicRoughnessMap_" + slotIndexB); //clear slot B if slot A was not defined / null
                    }
                }
                
                
                
            }
            
            
        }
        
        
    }

    public void editSlot(TerrainTextureSlot slot) {
        catalogManager.getTerrainTextureSlotCatalog().getAddCatalogItemTool().editExitingCatalogItem(catalogManager.getTerrainTextureSlotCatalog().findCatalogItem(slot.getName()));
     //   catalogManager.getTerrainTextureSlotCatalog().getAddCatalogItemTool().disableReturnToCatalogMenuOnNextClose();
    }

    private float getScaleAdjustedForTriPlanar(Material terrainMat, float originalScale) {
        boolean isTriPlanar = false;
        float alphaMapSize = 1;
        MatParamTexture alphaMapParam = terrainMat.getTextureParam("AlphaMap");
        if(alphaMapParam != null){
            alphaMapSize = alphaMapParam.getTextureValue().getImage().getHeight();
        }                    
        MatParam triPlanarParam = terrainMat.getParam("useTriPlanarMapping");
        if(triPlanarParam != null){
            isTriPlanar = (boolean) triPlanarParam.getValue();
        }

        if(isTriPlanar){
            originalScale = originalScale / alphaMapSize;
        }
        
        return originalScale;
    }

    
    
}
