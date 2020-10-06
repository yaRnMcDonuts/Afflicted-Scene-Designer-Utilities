/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter;

import ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog.TerrainTextureSlot;
import ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog.TextureSlotManager;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import com.jme3.util.SafeArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;



public class TerrainShaderConverter{

    public SimpleApplication app;
    public SimpleApplication getApp() { return app; }

    public TextureSlotManager textureSlotManager;
    public TextureSlotManager getTextureSlotManager() { return textureSlotManager;}

    public TerrainShaderMode getShaderModeOfTerrain(Terrain terrain) {
        String terrainMatDef = terrain.getMaterial().getMaterialDef().getAssetName();
        if(this.isPhongMat(terrainMatDef)){
            return TerrainShaderMode.PHONG;
        }else if(this.isPbrMat(terrainMatDef)){
            return TerrainShaderMode.PBR;
        }
        else if(this.isAdvancedPbrMat(terrainMatDef)){
            return TerrainShaderMode.ADVANCED_PBR;
        }
        
       return null;
        
        
    }


    
    public enum TerrainShaderMode{
        PHONG,
        PBR,
        ADVANCED_PBR;
    }
      
    private TextureArrayManager textureArrayManager;
    
    
    private final String defaultPhongShaderString = "Common/MatDefs/Terrain/TerrainLighting.j3md"; //default jme terrain matdef
    
    
    private final String defaultPbrShaderString = "MatDefs/shaders/PBRTerrain.j3md";  
    private final String defaultAdvancedPbrShaderString = "MatDefs/shaders/AdvancedPBRTerrain.j3md";   
      
    private String phongShaderAssetKeyString= "Common/MatDefs/Terrain/TerrainLighting.j3md"; //these are to be changed to your custom version of the phong,pbr, and advanced PBR terrain shader (if you have one)
    private String pbrShaderAssetKeyString;
    private String advancedPbrShaderAssetKeyString; //  <-- version of PBR shader that uses texture atlas
   
    public void setPhongShaderAssetKeyString(String phongShaderAssetKeyString) {    this.phongShaderAssetKeyString = phongShaderAssetKeyString; }
    public void setPbrShaderAssetKeyString(String pbrShaderAssetKeyString) {  this.pbrShaderAssetKeyString = pbrShaderAssetKeyString;  }
    public void setAdvancedPbrShaderAssetKeyString(String advancedPbrShaderAssetKeyString) {   this.advancedPbrShaderAssetKeyString = advancedPbrShaderAssetKeyString;  }
    
    private String assetFolder;
    
    

    //this needs set if you are using advanced pbr terrains! otherwise no catalog data will be loaded and terrain textures will be blank
    public void setTextureSlotManager(TextureSlotManager textureSlotManager) {
          this.textureSlotManager = textureSlotManager;
        
        textureArrayManager = textureSlotManager.getTextureArrayManager();
        
        
    }
    
    public TerrainShaderConverter(SimpleApplication app, String assetFolder) {
        this.app = app;
        this.assetFolder = assetFolder;
       
      
        
        
    }
    

    
    
    public ArrayList<String> customParamStrings = new ArrayList();
    public void addParamToConvert(String paramString) {  customParamStrings.add(paramString);  }   
    public void removeParamToConvert(String paramString) {  customParamStrings.remove(paramString);  } 
    
 //keep track of all texture slots, so that you still have the necissary data to convert from phong to advanced pbr   
    public ArrayList<TerrainTextureSlot> registeredTextureSlots;
    public void setRegisteredTextureSlots(ArrayList slotsLoadedFromFile){ registeredTextureSlots = slotsLoadedFromFile;  }

    public void addTextureSlotToRegistry(TerrainTextureSlot newSlot){ registeredTextureSlots.add(newSlot) ;}

    
    

          //example of how to register a texture
   //     registerPBRParamsForTexture( "Textures/crystalRockFace0.jpg", 0, .48f, .65f, 16);
    
   
   
    public boolean isPhongMat(String terrainMatDef) {
        if(terrainMatDef.equals(phongShaderAssetKeyString) || terrainMatDef.equals(defaultPhongShaderString)){
            return true;
        }else{
            return false;
        }
    
    }

    public boolean isPbrMat(String terrainMatDef) {
       if(terrainMatDef.equals(pbrShaderAssetKeyString)  || terrainMatDef.equals(defaultPbrShaderString)){
           return true;
       }else{
           return false;
       }
               
    }

    public boolean isAdvancedPbrMat(String terrainMatDef) {
        if(terrainMatDef.equals(advancedPbrShaderAssetKeyString) || terrainMatDef.equals(defaultAdvancedPbrShaderString)){
            return true;
        }
        else{
            return false;
        }
    }
    
    
    public void convertToPhong(Terrain terrain, AssetManager assetManager) {
        
        String terrainMatDef = terrain.getMaterial().getMaterialDef().getAssetName();
        if(isAdvancedPbrMat(terrainMatDef)){ //  advanced -> pbr
         //    createPhongTerrainMaterialFromAdvancedPBR(terrain.getMaterial(), assetManager);   
        }
        else if(isPbrMat(terrainMatDef)){// pbr -> phong
             Material newPhongMat = createPhongTerrainMaterialFromPBR(terrain.getMaterial(), assetManager);   
             
              setupTerrainMaterials((Spatial) terrain, newPhongMat);
        }
       
       
    }
    
    public void convertToPBR(Terrain terrain, AssetManager assetManager) {
        
        //only convert to PBR from a phong or from an advanced PBR matDef! 
        String terrainMatDef = terrain.getMaterial().getMaterialDef().getAssetName();
        if(isPhongMat(terrainMatDef)){ // phong -> pbr
             Material newPbrMat = createPbrTerrainMaterialFromPhong(terrain, terrain.getMaterial(), assetManager);
             setupTerrainMaterials((Spatial) terrain, newPbrMat);
             
        }
         else if(isAdvancedPbrMat(terrainMatDef)){  //advanced -> pbr
            Material newPbrMat = createPbrTerrainMaterialFromAdvancedPbr(terrain, terrain.getMaterial(), assetManager);
            setupTerrainMaterials((Spatial) terrain, newPbrMat);
        }
    }
    
    public void convertToAdvancedPBR(Terrain terrain, AssetManager assetManager) {
        
         String terrainMatDef = terrain.getMaterial().getMaterialDef().getAssetName();
         if(isPhongMat(terrainMatDef)){  // phong --> advanced
             
            
        }
        else if(isPbrMat(terrainMatDef)){ // pbr -> advanced
            Material newAdvancedPbrMat = createAdvancedPBRTerrainMaterialFromPBR(terrain, terrain.getMaterial(), assetManager);
            applyNewMaterial(newAdvancedPbrMat, terrain);
        }
    }
    

    //returns material, in case its needed for shizz like AfflictedZones quest, or if textures are needed elsewehre for stuff like a wind texture map
    public Material convertToPBROnLoad(Terrain terrain, AssetManager assetManager) {
        Material afflictedPbrMat = createPbrTerrainMaterialFromPhong(terrain, terrain.getMaterial(), assetManager);
        
        setupTerrainMaterials((Spatial) terrain, afflictedPbrMat);
        
        return afflictedPbrMat;
    }
    
    
     private void applyNewMaterial(Material newMat, Terrain terrain) {
        terrain.getMaterial().setKey(null); //clera old mat so the new one can be properly saved
        ((Spatial)terrain).setMaterial(newMat);
     }
   
     
    
    
   
     public Material createAdvancedPBRTerrainMaterialFromPBR(Terrain terrain, Material oldPbrMat, AssetManager assetManager) {         
         
            Material newAdvancedPbrMaterial = new Material((MaterialDef) assetManager.loadAsset(advancedPbrShaderAssetKeyString));
            
            TextureArray albedoTextureArray = null;
            TextureArray normalParallaxTextureArray = null;
            TextureArray metallicRoughnessAoEiTextureArray = null;
           
            
            ArrayList<Image> albedoTextureImages = new ArrayList();
             ArrayList<Image> normalParallaxTextureImages = new ArrayList();
              ArrayList<Image> metallicRoughnessAoEiTextureImages = new ArrayList();
            
            
            if(oldPbrMat.getParam("useTriPlanarMapping") != null){
               newAdvancedPbrMaterial.setParam("useTriPlanarMapping", VarType.Boolean, oldPbrMat.getParam("useTriPlanarMapping").getValue());
      //    afflictedTerrainMat.setParam("useTriPlanarMapping", VarType.Boolean, false);
            }
            
            
            int alphaMapSize = 1;
            
                    //set alpha maps
             for(int a = 0; a < 3; a ++){
                 MatParam param;
                String index = "";  //old terrain shader doesnt use 0 index for just the diffuse map
                if(a > 0){
                    index = "_" + a;
                }
                
               param = oldPbrMat.getParam("AlphaMap" + index);
                if(param != null){
                    newAdvancedPbrMaterial.setParam("AlphaMap" + index, VarType.Texture2D, (Texture) param.getValue());
                    
                    
                    alphaMapSize = ((Texture)param.getValue()).getImage().getHeight(); //height and width should already be the same for a valid alpha map
                }
                 
             }
             
            for(int i = 0; i < 12; i ++){
                MatParam param;
                
                
                String  index = "_" + i;  //advancedPbr and standard pbr share an index for all textures and params (only difference is phong diffuseMap)             
                                
                
               param = oldPbrMat.getParam("AlbedoMap" + index);
               
               String udKeyString = "TexSlot_" + i;
               
               String textureSlotName = ((Spatial)terrain).getUserData(udKeyString);
               //use the texture slot (if not null_) to fill in advanced data (such as metallicRoughnessMap) that is not available on the previous standard PBR material
               TerrainTextureSlot registeredTextureSlot = textureSlotManager.getTextureSlot(textureSlotName);
               
                if(param != null){  
                    
                    int albedoTexArrayIndex = - 1;
                    int normalParallaxTexArrayIndex = -1;
                    int metallicRoughnessAoEiTexArrayIndex = -1;   
                    
 
               
                    Texture albedoMapTexture = (Texture) param.getValue();
                    Image albedoImage = albedoMapTexture.getImage();
                    
                               
                    String texName = ((Texture)param.getValue()).getName();

                    String albedoTexArrayName = null;
                    String normalParallaxTexArrayName = null;
                    String metallicRoughnessAoEiTexArrayName = null;
                    //slot_0 is the slot used for naming and identifying a terrain's texture arrays        
                    if(i == 0 || albedoTextureArray == null){
                        albedoTexArrayName = textureSlotName + "_Albedo_TextureArray";
                        
                         
                        
                        
 //   *-*-*-* Albedo *-*-*-*                             
                    albedoTextureArray = textureArrayManager.getTextureArrayWithName(albedoTexArrayName);
                    if(albedoTextureArray == null){           
                            
                            //add the albedo image for slot_0 right away
                            albedoTextureImages.add(albedoImage);
                            albedoTextureArray = textureArrayManager.createAndRegisterNewTextureArray(albedoTextureImages, albedoTexArrayName);
                            
                            //set the texture index for this slot to 0 for being the first slot
                            albedoTexArrayIndex = i;
                            registeredTextureSlot.setAlbedoTexArray(albedoTextureArray);
                            registeredTextureSlot.setAlbedoTexArrayId(albedoTexArrayIndex);
                            
                        }                        
                        
                    }else{
                        Image albedoMapImage = albedoMapTexture.getImage();
                        
                        
                        albedoTexArrayIndex = textureArrayManager.addImageToArray(albedoTextureArray, albedoMapImage);
                        albedoTextureArray = textureArrayManager.getTextureArrayWithName(albedoTextureArray.getName());
                        registeredTextureSlot.setAlbedoTexArray(albedoTextureArray);
                        registeredTextureSlot.setAlbedoTexArrayId(albedoTexArrayIndex);

                        
                    }
                    if(albedoTexArrayIndex > -1){
                         newAdvancedPbrMaterial.setParam("AlbedoMap" + index, VarType.Int, albedoTexArrayIndex);
                         
                    }else{
                        newAdvancedPbrMaterial.setParam("AlbedoMap" + index, VarType.Int, 0);
                    }
                    
    //   *-*-*-* NORMAL / Parallax *-*-*-*       
 
                       //fill in normal/parallax map from textureSlot if the texture exists
                        Texture packedNormalParallaxTexture = registeredTextureSlot.getPackedNormalParallaxTexture();
                        Image packedNormalParallaxMapImage = null;
                        
                        if(packedNormalParallaxTexture == null){ ///fill in from old mat if no packed texture exists
                            param = oldPbrMat.getParam("NormalMap" + index);
                            if(param != null){
                                packedNormalParallaxTexture = (Texture) param.getValue();
                            }
                        }
                        if(packedNormalParallaxTexture == null){
                            packedNormalParallaxTexture = registeredTextureSlot.getNormalMapTexture(); // attempt to load standard normal map from slot it its still null
                        }
                        
                      // add images to texture array if the normalParallax texture exists
                        if(packedNormalParallaxTexture != null){
                            packedNormalParallaxMapImage = packedNormalParallaxTexture.getImage();
                            
                            if(normalParallaxTextureArray == null){ //create initial normalParallaxTexArray
                                normalParallaxTexArrayName = texName + "_NormalParallax_TextureArray"; //note: texName variable is the name of the albedo map matching this slot! for debuggin purposes to easily see which texArrays correlate
                                normalParallaxTextureImages.add(packedNormalParallaxMapImage);
                                 normalParallaxTextureArray = textureArrayManager.createAndRegisterNewTextureArray(normalParallaxTextureImages, normalParallaxTexArrayName);

                                //set the texture index for this slot to 0 for being the first slot
                                normalParallaxTexArrayIndex = 0;
                                registeredTextureSlot.setNormalParallaxTexArray(normalParallaxTextureArray);
                                
                                registeredTextureSlot.setPackedNormalParallaxTexArrayId(normalParallaxTexArrayIndex);
                               
                            }else{                        
                                normalParallaxTexArrayIndex = textureArrayManager.addImageToArray(normalParallaxTextureArray, packedNormalParallaxMapImage);
                                normalParallaxTextureArray = textureArrayManager.getTextureArrayWithName(normalParallaxTextureArray.getName());
                                registeredTextureSlot.setNormalParallaxTexArray(normalParallaxTextureArray);
                                registeredTextureSlot.setPackedNormalParallaxTexArrayId(normalParallaxTexArrayIndex);
                                
                            }
                            if(normalParallaxTexArrayIndex > -1){
                                 newAdvancedPbrMaterial.setParam("NormalMap" + index, VarType.Int, normalParallaxTexArrayIndex);
                                 //flag in shader that parallax is packed in this slot's normal map? 
                            }   
                        }
                        
                        
 //   *-*-*-* Metallic/Rough/Ao/Ei *-*-*-*    
 
                       //fill in normal/parallax map from textureSlot if the texture exists
                       
                        registeredTextureSlot.reloadTextures();
                        Texture packedMetallicRoughnessAoEiTexture = registeredTextureSlot.getPackedMetallicRoughnessAoEiTexture();
                        Image packedMetallicRoughnessAoEiMapImage = null;
                      // add images to texture array if the metallicRoughnessAoEi texture exists
                        if(packedMetallicRoughnessAoEiTexture != null){
                            packedMetallicRoughnessAoEiMapImage = packedMetallicRoughnessAoEiTexture.getImage();
                            
                            if(metallicRoughnessAoEiTextureArray == null){ //create initial metallicRoughnessAoEiTexArray
                                metallicRoughnessAoEiTexArrayName = texName + "_MetallicRoughnessAoEi_TextureArray"; //note: texName variable is the name of the albedo map matching this slot! for debuggin purposes to easily see which texArrays correlate
                                metallicRoughnessAoEiTextureImages.add(packedMetallicRoughnessAoEiMapImage);
                                 metallicRoughnessAoEiTextureArray = textureArrayManager.createAndRegisterNewTextureArray(metallicRoughnessAoEiTextureImages, metallicRoughnessAoEiTexArrayName);

                                //set the texture index for this slot to 0 for being the first slot
                                metallicRoughnessAoEiTexArrayIndex = 0;
                                registeredTextureSlot.setMetallicRoughnessAoEiTexArray(metallicRoughnessAoEiTextureArray);
                                
                                registeredTextureSlot.setPackedMetallicRoughnessAoEitextureId(metallicRoughnessAoEiTexArrayIndex);
                               
                            }else{                        
                                metallicRoughnessAoEiTexArrayIndex = textureArrayManager.addImageToArray(metallicRoughnessAoEiTextureArray, packedMetallicRoughnessAoEiMapImage);
                                metallicRoughnessAoEiTextureArray = textureArrayManager.getTextureArrayWithName(metallicRoughnessAoEiTextureArray.getName());
                                registeredTextureSlot.setMetallicRoughnessAoEiTexArray(metallicRoughnessAoEiTextureArray);
                                registeredTextureSlot.setPackedMetallicRoughnessAoEitextureId(metallicRoughnessAoEiTexArrayIndex);
                                
                            }
                            if(metallicRoughnessAoEiTexArrayIndex > -1){
                                 newAdvancedPbrMaterial.setParam("MetallicRoughnessMap" + index, VarType.Int, metallicRoughnessAoEiTexArrayIndex);
                                 //flag in shader that parallax is packed in this slot's normal map? 
                            }   
                        }
                   
                    

                    
                    
                    
                    
                    
                    
                     
                    
                    float scale = (float) oldPbrMat.getParam("AlbedoMap_" + i + "_scale").getValue();

                    scale = 4f;
                    
                      //adjust scale in case of tri-planar mapping...
                      
                        if(newAdvancedPbrMaterial.getParam("useTriPlanarMapping") != null){
                            if(newAdvancedPbrMaterial.getParam("useTriPlanarMapping").getValue().equals(true)){
                               scale = scale / alphaMapSize;
                            }
                        }
                    
                    
                    newAdvancedPbrMaterial.setParam("AlbedoMap_" + i + "_scale", VarType.Float, scale);

                    

                    newAdvancedPbrMaterial.setParam("Roughness" + index, VarType.Float, registeredTextureSlot.getRoughnessValue());
                    newAdvancedPbrMaterial.setParam("Metallic" + index, VarType.Float, registeredTextureSlot.getMetallicValue());
                    
                    
                    
                    //best way to do this? 
//                    if(registeredTextureSlot != null){
//                        Texture metallicRoughnessAoEiMap = registeredTextureSlot.getPackedMetallicRoughnessAoEiTexture();
//                        metallicRoughnessAoEiIndex = registeredTextureSlot.getPackedMetallicRoughnessAoEitextureId();
//                        
//                        
//                        
//                        if(metallicRoughnessAoEiIndex > -1){
//                             newAdvancedPbrMaterial.setParam("MetallicRoughnessMap" + index, VarType.Int, metallicRoughnessAoEiIndex);
//                        }
//                    }
                    
                    
                    
                    
//                    param = oldPbrMat.getParam("MetallicRoughnessMap" + index);
//                    if(param != null){
//                        Texture metallicRoughnessAoEiMapTexture = (Texture) param.getValue();
//                        Image metallicRoughnessAoEiMapImage = metallicRoughnessAoEiMapTexture.getImage();
//
//                        metallicRoughnessAoEiIndex = textureArrayManager.addImageToArray(metallicRoughnessAoEiTextureArray, metallicRoughnessAoEiMapImage);
//
//                        if(metallicRoughnessAoEiIndex > -1){
//                             newAdvancedPbrMaterial.setParam("MetallicRoughnessMap" + index, VarType.Int, metallicRoughnessAoEiIndex);
//                        }
//                       
                        
//                    }

////                  
                }
                
                
                
            }
            
            
             if(albedoTextureArray != null){
                newAdvancedPbrMaterial.setParam("AlbedoTextureArray", VarType.TextureArray, albedoTextureArray);
             }
             
             if(normalParallaxTextureArray != null && normalParallaxTextureArray.getImage() != null){
                  newAdvancedPbrMaterial.setParam("NormalParallaxTextureArray", VarType.TextureArray, normalParallaxTextureArray);
             }
             
             if(metallicRoughnessAoEiTextureArray != null  && metallicRoughnessAoEiTextureArray.getImage() != null){
                 newAdvancedPbrMaterial.setParam("MetallicRoughnessAoEiTextureArray", VarType.TextureArray, metallicRoughnessAoEiTextureArray);
             }
            
             
          
            
             for(String paramString : customParamStrings){
                    MatParam param = oldPbrMat.getParam(paramString);
                    if(param != null){
                       try{
                             newAdvancedPbrMaterial.setParam(paramString, param.getVarType(), param.getValue());
                        }
                        catch(Exception e){
                            System.err.println("TerrinShaderConverter Error: The MatParam "+ paramString + " is not defined in the MatDef for the new Phong Terrain Material");
                        }
                       
                    }
                }
             
            
          
             return newAdvancedPbrMaterial;
         
    }
    
     
     //note that this can only be done if you are using the USDUtilities library's catalog manager with the AfflictedSceneDesigner, otherwise there will be no standard PBR texture data to revert to, as it is lost when made into a textureArray
     public Material createPbrTerrainMaterialFromAdvancedPbr(Terrain terrain, Material oldMat, AssetManager assetManager){
           
            Material newPbrTerrainMat = new Material((MaterialDef) assetManager.loadAsset(pbrShaderAssetKeyString));
            
            if(oldMat.getParam("useTriPlanarMapping") != null){
               newPbrTerrainMat.setParam("useTriPlanarMapping", VarType.Boolean, oldMat.getParam("useTriPlanarMapping").getValue());
               
            }
            
            
            int alphaMapSize = 1;
                    //set alpha maps
             for(int a = 0; a < 3; a ++){
                 MatParam param;
                String index = ""; 
                if(a > 0){
                    index = "_" + a;
                }
                
               param = oldMat.getParam("AlphaMap" + index);
                if(param != null){
                    newPbrTerrainMat.setParam("AlphaMap" + index, VarType.Texture2D, (Texture) param.getValue());
                    
                    
                     alphaMapSize = ((Texture)param.getValue()).getImage().getHeight(); //height and width should already be the same for a valid alpha map
                }
                 
             }
           
             
                   //the texture arrays that get saved to an advanced pbr terrain can never be trusted/used, as they only seem to save the top-most image layer.. so the texture arrays need recreated 
             //every time an advanced pbr terrain is made, thus all the values need referenced from the catalog
             
            for(int i = 0; i < 12; i ++){
                MatParam param;
                
                String newIndex = "_" + i;
                
                String udKeyString = "TexSlot_" + i;
               
                String textureSlotName = ((Spatial)terrain).getUserData(udKeyString);
                //use the texture slot (if not null_) to get data from the advanced pbr terrain material that are lost in the texture array, but can be found by looking them up in the catalog by texture slot ID
                TerrainTextureSlot registeredTextureSlot = textureSlotManager.getTextureSlot(textureSlotName);
           
                //do a null check because if there is no texture slot, theres likely no chance the material will have any accurate params except for scale
                if(registeredTextureSlot != null){
                    


                    param = oldMat.getParam("AlbedoMap" + newIndex);
                     if(param != null){  

                         int albedoTexArrayIndex = (int) param.getValue();

                         
                            float scale = (float) oldMat.getParam("AlbedoMap_" + i + "_scale").getValue();


     //                       if(textureScales.get(texName) != null){
     //                           scale = textureScales.get(texName);
     //                       }
     //                       else{
                                scale = 4f;
      //                      }
      
                            scale = registeredTextureSlot.getScale();


                            //adjust scale in case of tri-planar mapping...
                             if(newPbrTerrainMat.getParam("useTriPlanarMapping") != null){
                                 if(newPbrTerrainMat.getParam("useTriPlanarMapping").getValue().equals(true)){
                                     scale = scale / alphaMapSize;
                                }
                             }


                            newPbrTerrainMat.setParam("AlbedoMap_" + i + "_scale", VarType.Float, scale);


                            //fill in albedo and normal map textures and Roughness and Metallic vals from terrain slot as loaded from the catalog

                            Texture albedoTex = registeredTextureSlot.getAlbedoTexture();
                            Texture normalTex = registeredTextureSlot.getNormalMapTexture();
                            if(normalTex == null){
                                normalTex = registeredTextureSlot.getPackedNormalParallaxTexture();
                            }
                            
                            if(albedoTex != null){
                                newPbrTerrainMat.setParam("AlbedoMap" + newIndex, VarType.Texture2D, albedoTex);
                            }
                            if(normalTex != null){
                                newPbrTerrainMat.setParam("NormalMap" + newIndex, VarType.Texture2D, normalTex);
                            }
                            

                              newPbrTerrainMat.setParam("Roughness" + newIndex, VarType.Float, registeredTextureSlot.getRoughnessValue());
                             newPbrTerrainMat.setParam("Metallic" + newIndex, VarType.Float, registeredTextureSlot.getMetallicValue());
                    


                        }

                     


                 }
            }
            

            
            for(String paramString : customParamStrings){
                    MatParam param = oldMat.getParam(paramString);
                    if(param != null){
                        try{
                             newPbrTerrainMat.setParam(paramString, param.getVarType(), param.getValue());
                        }
                        catch(Exception e){
                            System.err.println("TerrinShaderConverter Error: The MatParam "+ paramString + " is not defined in the MatDef for the new Phong Terrain Material");
                        }
                       
                    }
                }
            
            return newPbrTerrainMat;
    }
    
    
       public Material createPbrTerrainMaterialFromPhong(Terrain terrain, Material oldMat, AssetManager assetManager){
           
            Material newPbrTerrainMat = new Material((MaterialDef) assetManager.loadAsset(pbrShaderAssetKeyString));
            
            
            
            if(oldMat.getParam("useTriPlanarMapping") != null){
               newPbrTerrainMat.setParam("useTriPlanarMapping", VarType.Boolean, oldMat.getParam("useTriPlanarMapping").getValue());
               
            }
            
            
            int alphaMapSize = 1;
                    //set alpha maps
             for(int a = 0; a < 3; a ++){
                 MatParam param;
                String index = "";  //terrain shader doesnt use 0 index for just the diffuse map
                if(a > 0){
                    index = "_" + a;
                }
                
               param = oldMat.getParam("AlphaMap" + index);
                if(param != null){
                    newPbrTerrainMat.setParam("AlphaMap" + index, VarType.Texture2D, (Texture) param.getValue());
                    
                    
                     alphaMapSize = ((Texture)param.getValue()).getImage().getHeight(); //height and width should already be the same for a valid alpha map
                }
                 
             }
             
              
           
            for(int i = 0; i < 12; i ++){
                MatParam param;
                String oldindex = "";  //terrain shader doesnt use 0 index for just the diffuse map
                if(i > 0){
                     oldindex = "_" + i;
                }
                
                String newIndex = "_" + i;
                
                
                  String udKeyString = "TexSlot_" + i;
               String textureSlotName = ((Spatial)terrain).getUserData(udKeyString);
                //use the texture slot (if not null_) to get data from the advanced pbr terrain material that are lost in the texture array, but can be found by looking them up in the catalog by texture slot ID
                TerrainTextureSlot registeredTextureSlot = textureSlotManager.getTextureSlot(textureSlotName);
                
                
               param = oldMat.getParam("DiffuseMap" + oldindex);
                if(param != null){  
                    String texName = ((Texture)param.getValue()).getName();
                       newPbrTerrainMat.setParam("AlbedoMap" + newIndex, VarType.Texture2D, param.getValue());
                       float scale = (float) oldMat.getParam("DiffuseMap_" + i + "_scale").getValue();


//                       if(textureScales.get(texName) != null){
//                           scale = textureScales.get(texName);
//                       }
//                       else{
                           scale = 4f;
 //                      }


                       //adjust scale in case of tri-planar mapping...
                        if(newPbrTerrainMat.getParam("useTriPlanarMapping") != null){
                            if(newPbrTerrainMat.getParam("useTriPlanarMapping").getValue().equals(true)){
                                scale = scale / alphaMapSize;
                           }
                        }


                       newPbrTerrainMat.setParam("AlbedoMap_" + i + "_scale", VarType.Float, scale);


                       //fill in extra data (aka roughness and metallic values) from saved TextureSlots (if possible)
                       

                       
                       

                   }
                    param = oldMat.getParam("NormalMap" + oldindex);
                    if(param != null){
                        newPbrTerrainMat.setParam("NormalMap" + newIndex, VarType.Texture2D, param.getValue());
                    }
                    
                    
                                    //do a null check because if there is no texture slot, theres likely no chance the material will have any accurate params except for scale
                if(registeredTextureSlot != null){

                    newPbrTerrainMat.setParam("Roughness" + newIndex, VarType.Float, registeredTextureSlot.getRoughnessValue());
                    newPbrTerrainMat.setParam("Metallic" + newIndex, VarType.Float, registeredTextureSlot.getMetallicValue());
                }
                else{
                    newPbrTerrainMat.setParam("Roughness" + newIndex, VarType.Float, 0.98f);
                    newPbrTerrainMat.setParam("Metallic" + newIndex, VarType.Float, 0.01f);
                }
                    
                   
            }
            

            
            for(String paramString : customParamStrings){
                    MatParam param = oldMat.getParam(paramString);
                    if(param != null){
                        try{
                             newPbrTerrainMat.setParam(paramString, param.getVarType(), param.getValue());
                             
                        }
                        catch(Exception e){
                            System.err.println("TerrinShaderConverter Error: The MatParam "+ paramString + " is not defined in the MatDef for the new Phong Terrain Material");
                        }
                       
                    }
                }
            
            return newPbrTerrainMat;
    }
       
     public Material createPhongTerrainMaterialFromPBR(Material oldPbrMat, AssetManager assetManager){
           
            Material phongTerrainMat = new Material((MaterialDef) assetManager.loadAsset(phongShaderAssetKeyString));
            
            
            
            
            if(oldPbrMat.getParam("useTriPlanarMapping") != null){
               phongTerrainMat.setParam("useTriPlanarMapping", VarType.Boolean, oldPbrMat.getParam("useTriPlanarMapping").getValue());
      //    afflictedTerrainMat.setParam("useTriPlanarMapping", VarType.Boolean, false);
            }
            
            
            int alphaMapSize = 1;
            
                    //set alpha maps
             for(int a = 0; a < 3; a ++){
                 MatParam param;
                String index = "";  //old terrain shader doesnt use 0 index for just the diffuse map
                if(a > 0){
                    index = "_" + a;
                }
                
                
                
               param = oldPbrMat.getParam("AlphaMap" + index);
                if(param != null){
                    phongTerrainMat.setParam("AlphaMap" + index, VarType.Texture2D, (Texture) param.getValue());
                    
                    
                    alphaMapSize = ((Texture)param.getValue()).getImage().getHeight(); //height and width should already be the same for a valid alpha map
                }
                 
             }
           
            for(int i = 0; i < 12; i ++){
                MatParam param;
                String oldIndex = "";  //old terrain shader doesnt use 0 index for just the diffuse map
                if(i > 0){
                     oldIndex = "_" + i;
                }
                
                String newIndex = "_" + i;
                
               param = oldPbrMat.getParam("AlbedoMap" + newIndex);
               
               
                if(param != null){  
                    
                    String texName = ((Texture)param.getValue()).getName();
                    
                    phongTerrainMat.setParam("DiffuseMap" + oldIndex, VarType.Texture2D, param.getValue());
                    float scale = (float) oldPbrMat.getParam("AlbedoMap_" + i + "_scale").getValue();
                    
//                    
//                    if(textureScales.get(texName) != null){
//                        scale = textureScales.get(texName);
//                    }
//                    else{
                        scale = 4f;
//                    }
                    
                      //adjust scale in case of tri-planar mapping...
                      
                        if(phongTerrainMat.getParam("useTriPlanarMapping") != null){
                            if(phongTerrainMat.getParam("useTriPlanarMapping").getValue().equals(true)){
                               scale = scale / alphaMapSize;
                            }
                        }
                    
                    
                    phongTerrainMat.setParam("DiffuseMap_" + i + "_scale", VarType.Float, scale);
                                    
                    
                //sets afflictionMode, roughness, and metallic params for AfflictedShader
//                    if(textureAfflictionModeValues.get(texName) != null){
//                        int afflictionMode = 0;
//                        afflictionMode = textureAfflictionModeValues.get(texName);
//                        phongTerrainMat.setParam("AfflictionMode_" + i, VarType.Int, afflictionMode);
//                    }
////                  
                }
                
                 param = oldPbrMat.getParam("NormalMap" + newIndex);
                if(param != null){
                    phongTerrainMat.setParam("NormalMap" + oldIndex, VarType.Texture2D, param.getValue());
                }
                
                
                
            }
          
            
             for(String paramString : customParamStrings){
                    MatParam param = oldPbrMat.getParam(paramString);
                    if(param != null){
                        try{
                             phongTerrainMat.setParam(paramString, param.getVarType(), param.getValue());
                             
                        }
                        catch(Exception e){
                            System.err.println("TerrinShaderConverter Error: The MatParam "+ paramString + " is not defined in the MatDef for the new Phong Terrain Material");
                        }
                       
                    }
                }
            
            
            
            return phongTerrainMat;
    }
       
    

       
//       // code for upgrading to advanced pbr shader
//     
//     public TextureArray getTextureArray(String texArrayName){
//         TextureArray texArray = registeredTextureArrays.get(texArrayName) ;
//         
//         if(texArray == null){
//             texArray = loadRegisteredTextureArray(texArrayName);
//             registeredTextureArrays.put(texArrayName, texArray);
//             
//             
//             
//         }
//         return texArray;
//    }
//
//
//    public TextureArray loadRegisteredTextureArray(){
//        
//        //read from .json that is named after 
//        
//        return null;
//    }
//      
    
    private TextureArray loadRegoiteredTextureArray(String texArrayName) {
       
        return null;
    }
       
       
    public void setupTerrainMaterials(Spatial spatial, Material material){
        
        //easier way.. but o well too late
//       spatial.setMaterial(material);  



        if(spatial instanceof Node){
            
            SafeArrayList<Spatial> terrainChildren = (SafeArrayList<Spatial>) ((Node)spatial).getChildren();
            for(Spatial nodeSpatial: terrainChildren){
                setupTerrainMaterials(nodeSpatial, material);
            }
        }else{
            
            Geometry patchGeo = (Geometry)spatial;
            patchGeo.setMaterial(material);
            
        }
        
    }
    
    public Texture makeBlankAlphaMap(int width, Terrain terrain, int alphaMapIndex, String imageName) throws IOException{
        
            
        
        BufferedImage alphaBlend = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
            if (alphaMapIndex == 0) {
                // the first alpha level should be opaque so we see the first texture over the whole terrain
                for (int h=0; h<width; h++)
                    for (int w=0; w<width; w++)
                        alphaBlend.setRGB(w, h, 0x00FF0000);//argb
            }
            File textureFolder = new File(assetFolder + "/Textures/");
            if (!textureFolder.exists()) {
                if (!textureFolder.mkdir()) {
                    throw new IOException("Could not create the Texture Folder (assets/Textures)!");
                }
            }
            
            File alphaFolder = new File(assetFolder + "/Textures/terrain-alpha/");
            if (!alphaFolder.exists()) {
                if (!alphaFolder.mkdir()) {
                    throw new IOException("Could not create the Terrain Alpha Folder (assets/Textures/terrain-alpha)!");
                }
            }
            File alphaFolderTemp = new File(assetFolder + "/Textures/terrain-alpha/tempAlphaMaps/");
            if (!alphaFolderTemp.exists()) {
                if (!alphaFolderTemp.mkdir()) {
                    throw new IOException("Could not create the Terrain Alpha Folder (assets/Textures/terrain-alpha/tempAlphaMaps)!");
                }
            }
            
            String alphaBlendFileName = "/Textures/terrain-alpha/"+ imageName;
            File alphaImageFile = new File(assetFolder+alphaBlendFileName);
            ImageIO.write(alphaBlend, "png", alphaImageFile);
            Texture tex = app.getAssetManager().loadAsset(new TextureKey(alphaBlendFileName, false));
           
            
            
            return tex;
    }

   
   

}

