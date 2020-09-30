/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog;

import ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter.TerrainShaderConverter.TerrainShaderMode;
import ASD_Utilities.TextureUtilities.TextureChannelPacker.TextureChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.input.MouseInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import com.jme3.texture.image.ColorSpace;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryan
 */ 
public class TerrainTextureSlot {
 
    
    private String name;
    public String getName(){ return name; }
    
    
    //change the mode of the texture slot when its being displayed for different terrain shader types (phong, pbr, or pbr+)
    public TerrainShaderMode shaderMode = TerrainShaderMode.PHONG;
    public void setTerrainShaderMode(TerrainShaderMode terrainShaderMode){  shaderMode = terrainShaderMode ;}
    public TerrainShaderMode getShaderMode() {  return shaderMode;  }
    
 //advanced pbr tex array and IDs   
    
    private int albedoTexArrayId = -1;
    private int packedNormalParallaxTexArrayId = -1;
    private int packedMetallicRoughnessAoEiTexArrayId = - 1;

    //tex arrays that this slot uses - will be null if the terrain has never been advanced pbr
    private TextureArray albedoTexArray;
    private TextureArray normalParallaxTexArray;
    private TextureArray metallicRoughnessAoEiTexArray;    
    
    
    
  //Textures  
    private Texture albedoTexture;
    
    private Texture normalMapTexture;
    private Texture parallaxTexture;
   
    
    private Texture packedNormalParallaxTexture;
    //Normal      - rgb
    //Parallax    - a    
    
    private Texture metallicTexture;
    private Texture roughnessTexture;
    private Texture ambientOcclusionTexture; 
    private Texture emissiveIntensityTexture;
    
    private Texture packedMetallicRoughnessAoEiTexture; 
    // AO        - r
    // Roughness - g
    // Metallic  - b
    // Emissive  - a 
    
//float params 
    
    private float scale = 1;
    
    

    
    
    private float metallicValue = 0;
    private float roughnessValue = 0;    
    
    //values that are multiplied by the MRAOEi map in the shader when using advanced pbr terrain shader.
    private float advancedRoughnessValue = 1.0f;
    private float advancedMetallicValue = 1.0f;
    
    
 //afflicted forests specific var
    private int afflictionMode = 0;
    
//terrains that use this texture slot     
    private ArrayList<Terrain> terrainsRegisteredToThisSlot = new ArrayList();
    private HashMap<Terrain, Integer> terrainsRegisteredToThisSlotMap = new HashMap();
    public ArrayList<Terrain> getTerrainsPairedToSlot(){ return terrainsRegisteredToThisSlot; }
    public void registerTerrainInSlot(Terrain terrain, int slotForTerrain) {
        terrainsRegisteredToThisSlotMap.put(terrain, slotForTerrain);
        terrainsRegisteredToThisSlot.add(terrain);
        
        
        app.enqueue(() ->{
            this.reloadTextures();
        });
        
    
    }
    
    public void mapSlotToShader(Terrain terrain, int slotIndexForTerrainMat){
        Material terrainMat = terrain.getMaterial();
        reloadTextures();
        
        float adjustedScale = scale;
        //adjust scale for triPlanar terrains
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
            adjustedScale = scale / alphaMapSize;
        }
        
        if(shaderMode.equals(TerrainShaderMode.PHONG)){
                
            String diffuseMatDefString = "DiffuseMap";
            if(slotIndexForTerrainMat > 0){  // 0 index doesn't include 0 in the phong shader var name
                diffuseMatDefString += "_" + slotIndexForTerrainMat;
            }

            if(albedoTexture != null){
                terrainMat.setTexture(diffuseMatDefString, albedoTexture);
            }
            else if(terrainMat.getParam(diffuseMatDefString) != null){
                terrainMat.clearParam(diffuseMatDefString);
            }
            
            terrainMat.setFloat("DiffuseMap_" + slotIndexForTerrainMat+ "_scale", adjustedScale);
            
            
            String normalMapMatParamString = "NormalMap";
            if(slotIndexForTerrainMat > 0){  // 0 index doesn't include 0 in the phong shader var name
                normalMapMatParamString += "_" + slotIndexForTerrainMat;
            }
            if(normalMapTexture != null){
                terrainMat.setTexture(normalMapMatParamString, normalMapTexture);
            }
            else if(terrainMat.getParam(normalMapMatParamString) != null){
                terrainMat.clearParam(normalMapMatParamString);
            }
                
        }else if(shaderMode.equals(TerrainShaderMode.PBR)){
            String albedoMatParamString = "AlbedoMap_" + slotIndexForTerrainMat;
            if(albedoTexture != null){
                terrainMat.setTexture(albedoMatParamString, albedoTexture);
            } else if(terrainMat.getParam(albedoMatParamString) != null){
                terrainMat.clearParam(albedoMatParamString);
            }
            
            terrainMat.setFloat("AlbedoMap_" + slotIndexForTerrainMat+ "_scale", adjustedScale);
            
            String normalMapMatParamString = "NormalMap_" + slotIndexForTerrainMat;
             if(packedNormalParallaxTexture != null){
                 terrainMat.setTexture(normalMapMatParamString, packedNormalParallaxTexture);
                 //flag shader to indicate that packed nrml/plx is being used?
             }
             else if(normalMapTexture != null){
                 terrainMat.setTexture(normalMapMatParamString, normalMapTexture);
             }else if(terrainMat.getParam(normalMapMatParamString) != null){
                terrainMat.clearParam(normalMapMatParamString);
            }

            terrainMat.setFloat("Roughness_" + slotIndexForTerrainMat, roughnessValue);
            terrainMat.setFloat("Metallic_" + slotIndexForTerrainMat, metallicValue);
            
            terrainMat.setInt("AfflictionMode_" + slotIndexForTerrainMat, afflictionMode);


        }
        else if(shaderMode.equals(TerrainShaderMode.ADVANCED_PBR)){

            String albedoTexName = ""; //albedo texName is used for naming the normal/Plx and MRAoEi maps
            String albedoMatParamString = "AlbedoMap_" + slotIndexForTerrainMat;
             if(albedoTexture != null){
                 albedoTexName = albedoTexture.getName();
                 
                 if(albedoTexArray == null){
                     String albedoTexArrayName = albedoTexName + "_Albedo_TextureArray";
                     List<Image> imageList = new ArrayList();
                     imageList.add(albedoTexture.getImage());
                     albedoTexArray = textureSlotManager.getTextureArrayManager().createAndRegisterNewTextureArray(imageList, albedoTexArrayName);
                 }else{                     
                     albedoTexArrayId = textureSlotManager.getTextureArrayManager().addImageToArray(albedoTexArray, albedoTexture.getImage());
                 }
                
                terrainMat.setInt(albedoMatParamString, albedoTexArrayId);
                this.albedoTexArray = textureSlotManager.getTextureArrayManager().getTextureArrayWithName(albedoTexArray.getName());
                terrainMat.setParam("AlbedoTextureArray", VarType.TextureArray, albedoTexArray);
             }
             else if(albedoTexArrayId < 0 && terrainMat.getParam(albedoMatParamString) != null){        
                 terrainMat.clearParam(albedoMatParamString);
             }
            
            terrainMat.setFloat("AlbedoMap_" + slotIndexForTerrainMat+ "_scale", adjustedScale);
            
            String normalMapMatParamString = "NormalMap_" + slotIndexForTerrainMat;
             if(packedNormalParallaxTexture != null){
                 
                 if(normalParallaxTexArray == null){
                     String normalParallaxTexArrayName = albedoTexName + "_NormalParallax_TextureArray";
                     List<Image> imageList = new ArrayList();
                     imageList.add(packedNormalParallaxTexture.getImage());
                     normalParallaxTexArray = textureSlotManager.getTextureArrayManager().createAndRegisterNewTextureArray(imageList, normalParallaxTexArrayName);
                 }else{
                    packedNormalParallaxTexArrayId = textureSlotManager.getTextureArrayManager().addImageToArray(normalParallaxTexArray, packedNormalParallaxTexture.getImage());
                 }
                 
                 terrainMat.setInt(normalMapMatParamString, packedNormalParallaxTexArrayId);
                 this.normalParallaxTexArray = textureSlotManager.getTextureArrayManager().getTextureArrayWithName(normalParallaxTexArray.getName());
                terrainMat.setParam("NormalParallaxTextureArray", VarType.TextureArray, normalParallaxTexArray);
             }
             else if(packedNormalParallaxTexArrayId < 0 && terrainMat.getParam(normalMapMatParamString) != null){
                 terrainMat.clearParam(normalMapMatParamString);
             }
             
             
             String metallicRoughnessAoEiParamString = "MetallicRoughnessMap_" + slotIndexForTerrainMat;
            if(packedMetallicRoughnessAoEiTexture != null){
                 if(metallicRoughnessAoEiTexArray == null){
                     String metallicRoughnessAoEiTexArrayName = albedoTexName + "_MetallicRoughnessAoEi_TextureArray";
                     List<Image> imageList = new ArrayList();
                     imageList.add(packedMetallicRoughnessAoEiTexture.getImage());
                     metallicRoughnessAoEiTexArray = textureSlotManager.getTextureArrayManager().createAndRegisterNewTextureArray(imageList, metallicRoughnessAoEiTexArrayName);
                 }else{
                    packedMetallicRoughnessAoEiTexArrayId = textureSlotManager.getTextureArrayManager().addImageToArray(metallicRoughnessAoEiTexArray, packedMetallicRoughnessAoEiTexture.getImage());
                 }
                 
                 terrainMat.setInt(metallicRoughnessAoEiParamString, packedMetallicRoughnessAoEiTexArrayId);
                 this.metallicRoughnessAoEiTexArray = textureSlotManager.getTextureArrayManager().getTextureArrayWithName(metallicRoughnessAoEiTexArray.getName());
                terrainMat.setParam("MetallicRoughnessAoEiTextureArray", VarType.TextureArray, metallicRoughnessAoEiTexArray);
            }else if(packedMetallicRoughnessAoEiTexArrayId < 0 && terrainMat.getParam(metallicRoughnessAoEiParamString) != null){
                terrainMat.clearParam(metallicRoughnessAoEiParamString);
            }
            
            terrainMat.setFloat("Roughness_" + slotIndexForTerrainMat, advancedRoughnessValue);
            terrainMat.setFloat("Metallic_" + slotIndexForTerrainMat, advancedMetallicValue);
            
            terrainMat.setInt("AfflictionMode_" + slotIndexForTerrainMat, afflictionMode);

        }
    }

    
    
    
  //interface  
    private final Container slotContainer = new Container();
    
    
    private Label slotNameLabel;
    
    private Checkbox selectionCheckbox;
    
    private Container slotValuesContainer;
    
    private TextureSlotManager textureSlotManager;
    
    private SimpleApplication app;
    
    
    //used for scaling the texture slot interface if you choose to use them visually
    private final float screenWidth, screenHeight;
    
    private TbtQuadBackgroundComponent defaultquadBackground;
    
    public TerrainTextureSlot(String name, TextureSlotManager textureSlotManager, float screenWidth, float screenHeight){
        this.name = name;
        this.textureSlotManager = textureSlotManager;
        
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        
        app = textureSlotManager.getApp();
        
        Container tempContainer = new Container();
        defaultquadBackground = (TbtQuadBackgroundComponent) tempContainer.getBackground().clone();
        
        
        slotNameLabel = new Label(name);
        
        slotContainer.addChild(slotNameLabel);
        
    //init all image/value containers/text fields    
    
    
        albedoIcon = makeIconContainer();
        normalMapIcon = makeIconContainer();
        parallaxMapIcon = makeIconContainer();
        aoMapIcon = makeIconContainer();
        roughnessMapIcon = makeIconContainer();
        metallicMapIcon = makeIconContainer();
        eiMapIcon = makeIconContainer();
        packedNormalParallaxMapIcon  = makeIconContainer();
        packedMetallicRoughnessAoEiMapIcon = makeIconContainer();
        
        scaleValueTextField = new TextField("1.0");
        roughnessValueTextField = new TextField("0.0");
        metallicValueTextField = new TextField("0.0");
        afflictionModeValueTextField = new TextField("1");
        
        roughnessValueVersionedReference = roughnessValueTextField.getDocumentModel().createCaratReference();
        metallicValueVersionedReference = metallicValueTextField.getDocumentModel().createCaratReference();      
        scaleValueVersionedReference = scaleValueTextField.getDocumentModel().createCaratReference();
        afflictionModeValueVersionedReference = afflictionModeValueTextField.getDocumentModel().createCaratReference();
      
        
        
        roughnessValueTextField.setPreferredSize(new Vector3f(36, 20, 1));
        metallicValueTextField.setPreferredSize(new Vector3f(36, 20, 1));
        afflictionModeValueTextField.setPreferredSize(new Vector3f(30, 20, 1));
        scaleValueTextField.setPreferredSize(new Vector3f(36, 20, 1));
        
        roughnessValueTextField.setInsets(new Insets3f(6,2,9,2));
        metallicValueTextField.setInsets(new Insets3f(6,2,9,2));
        afflictionModeValueTextField.setInsets(new Insets3f(6,2,9,2));
        scaleValueTextField.setInsets(new Insets3f(6,4,9,2));
        
        
        
        slotValuesContainer = new Container();
        slotContainer.addChild(slotValuesContainer);
        
        selectionCheckbox = new Checkbox("");
        selectionCheckbox.setPreferredSize(new Vector3f(.02f * screenWidth, .04f * screenHeight, 1));
        selectionCheckbox.setInsets(new Insets3f(0, screenWidth * .005f, 0,0));
        
        slotValuesContainer.addChild(selectionCheckbox);
       
        
        slotValuesContainer.setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.None));
        
        
      
        selectionCheckbox.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            
            app.enqueue(() -> {
                selectionCheckbox.setChecked(true);
            });
           
           
           textureSlotManager.setSelectedTextureSlot(this);
            
        });
        
        
    }
    
    private VersionedReference scaleValueVersionedReference;
    private VersionedReference afflictionModeValueVersionedReference;
    private VersionedReference roughnessValueVersionedReference;
    private VersionedReference metallicValueVersionedReference;
    
    public void update(float tpf){
        
       
        if(scaleValueVersionedReference.update()){
            String scaleValueString = scaleValueTextField.getText();
            if(isValidTextFieldEntry(scaleValueString, scaleValueTextField)){
             
                
                
                scale = Float.parseFloat(scaleValueTextField.getText());

                for (int t = 0; t < terrainsRegisteredToThisSlot.size(); t++){
                    Terrain terrain = terrainsRegisteredToThisSlot.get(t);
                    
                    String matDefName = terrain.getMaterial().getMaterialDef().getAssetName();
                       
                    String paramStringKey = "";
                    if(textureSlotManager.getTerrainShaderConverter().isPhongMat(matDefName)){
                        paramStringKey = "DiffuseMap_";
                    }
                    else{
                        paramStringKey = "AlbedoMap_";
                    }

                    Material terrainMat = terrain.getMaterial();

                    int slotForTerrain = terrainsRegisteredToThisSlotMap.get(terrain);

                    
                    paramStringKey += slotForTerrain + "_scale";

                    
                    //adjust scale for triPlanar terrains
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
                        terrainMat.setFloat(paramStringKey, scale / alphaMapSize);
                    }
                    else{
                        terrainMat.setFloat(paramStringKey, scale);
                    }
                    

                    

                    ((TerrainQuad)terrain).setMaterial(terrainMat);
                }
            }

        }

         //update roughness values from text boxes if not using phong mode
         
            
            if(roughnessValueVersionedReference.update()){
                String roughnessValueString = roughnessValueTextField.getText();
                if(isValidTextFieldEntry(roughnessValueString, roughnessValueTextField)){
                    float roughVal = Float.parseFloat(roughnessValueTextField.getText());

                    for (int t = 0; t < terrainsRegisteredToThisSlot.size(); t++){
                        Terrain terrain = terrainsRegisteredToThisSlot.get(t);

                        Material terrainMat = terrain.getMaterial();
                        
                        String matDefName = terrain.getMaterial().getMaterialDef().getAssetName();
                        if(! textureSlotManager.getTerrainShaderConverter().isPhongMat(matDefName)){

                            int slotForTerrain = terrainsRegisteredToThisSlotMap.get(terrain);

                            String paramString = "Roughness_" + slotForTerrain;    
    

                            
                            if(TerrainShaderMode.PBR.equals(shaderMode)){
                                roughnessValue = roughVal;
                                terrainMat.setFloat(paramString, roughnessValue);
                                
                            }else if(TerrainShaderMode.ADVANCED_PBR.equals(shaderMode)){
                                advancedRoughnessValue = roughVal;
                                terrainMat.setFloat(paramString, advancedRoughnessValue);
                            }            
                            
                            

                            ((TerrainQuad)terrain).setMaterial(terrainMat);
                        }
                    }
                }

            }
            if(metallicValueVersionedReference.update()){

                String metallicValueString = metallicValueTextField.getText();
                if(isValidTextFieldEntry(metallicValueString, metallicValueTextField)){
                    
                    float metVal = Float.parseFloat(metallicValueString);
                    for (int t = 0; t < terrainsRegisteredToThisSlot.size(); t++){
                        Terrain terrain = terrainsRegisteredToThisSlot.get(t);

                        Material terrainMat = terrain.getMaterial();
                        
                        String matDefName = terrain.getMaterial().getMaterialDef().getAssetName();
                        if(! textureSlotManager.getTerrainShaderConverter().isPhongMat(matDefName)){

                            int slotForTerrain = terrainsRegisteredToThisSlotMap.get(terrain);

                            String paramString = "Metallic_" + slotForTerrain;    

                            if(TerrainShaderMode.PBR.equals(shaderMode)){
                                metallicValue = metVal;
                                terrainMat.setFloat(paramString, metallicValue);
                                
                            }else if(TerrainShaderMode.ADVANCED_PBR.equals(shaderMode)){
                                advancedMetallicValue = metVal;
                                terrainMat.setFloat(paramString, advancedMetallicValue);
                            }                            
                            
                            ((TerrainQuad)terrain).setMaterial(terrainMat);
                        }
                    }
                    
                    
                }
               

            }

        
        
        
       String afflictionModeValueString = afflictionModeValueTextField.getText();
            if(isValidTextFieldEntry(afflictionModeValueString, afflictionModeValueTextField)){
                
                
                afflictionMode = (int) Float.parseFloat(afflictionModeValueTextField.getText());

                for (int t = 0; t < terrainsRegisteredToThisSlot.size(); t++){
                    Terrain terrain = terrainsRegisteredToThisSlot.get(t);

                    Material terrainMat = terrain.getMaterial();

                    int slotForTerrain = terrainsRegisteredToThisSlotMap.get(terrain);

                    String paramString = "AfflictionMode_" + slotForTerrain; 
                    if(terrainMat.getParam(paramString) != null){
                        
                        terrainMat.setInt(paramString, afflictionMode);

                        ((TerrainQuad)terrain).setMaterial(terrainMat);
                    }
                      

                }
                
                afflictionModeValueTextField.setText("" + afflictionMode);
            }
    }
    
    //test strings to make sure they can be parsed to float, and undo any invalid letters entered
    private boolean isValidTextFieldEntry(String string, TextField textField){
        
    //    String lastLetter = string.substring(0, string.length() - 1);
        
        
        try{
          float numberValue = Float.parseFloat(string);  
            return true;
            
        }catch(Exception e){
            
            if(string.length() > 0){
                 textField.setText(string.substring(0, string.length() - 1));
            }
            else{
                textField.setText("0");
            }
           
            
            
        }
        
        
        return false;
    }
    
    public void unselect() {
        selectionCheckbox.setChecked(false);
    }
    
    public void showForPhong(Material newMat){
        
        
        
    }
    
    private void editThisSlot(){
        textureSlotManager.editSlot(this);
    }
    
    private Container makeIconContainer(){
        Vector3f textureIconSize = new Vector3f(screenWidth * .026f, screenHeight * .04f, 1);
        Insets3f insets = new Insets3f(screenHeight * 0.0039f, screenWidth * 0.002f, screenHeight * 0.001f, screenWidth * 0.002f);
        
        Container iconContainer = new Container();
        
        iconContainer.setInsets(insets);
        iconContainer.setPreferredSize(textureIconSize);
  //      
        
        
           MouseEventControl.addListenersToSpatial(iconContainer,
            new DefaultMouseListener() {
                @Override
                protected void click( MouseButtonEvent event, Spatial target, Spatial capture ) {
                    
                    
                    if(target != null){
                        if( event.getButtonIndex() == MouseInput.BUTTON_LEFT ) {
                            target.move(0, 0.1f, 0);
                        } else {
                            target.move(0, -0.1f, 0);
                        }   
                    }
                    
                    
                    editThisSlot();
                }
                    
                @Override
                public void mouseEntered( MouseMotionEvent event, Spatial target, Spatial capture ) {
                    
                    iconContainer.setAlpha(1.0f);
                    
                    QuadBackgroundComponent border = new QuadBackgroundComponent();
                    border.setMargin(-11, -11);
                    
                    iconContainer.setBorder(border);
                }

                @Override
                public void mouseExited( MouseMotionEvent event, Spatial target, Spatial capture ) {
                    iconContainer.setAlpha(1.0f);
                    iconContainer.setBorder(null);
                }                        
            });
        
        
  
        return iconContainer;
        
    }
    

    public void unregistereTerrainFromSlot(Terrain terrain, int i) {
        terrainsRegisteredToThisSlotMap.remove(terrain);
        terrainsRegisteredToThisSlot.remove(terrain);
        
    }
    
    public Container getSlotContainer(){ 
        if(shaderMode.equals(TerrainShaderMode.PHONG)){
            return(getContainerForPhong());
        }
        else if(shaderMode.equals(TerrainShaderMode.PBR)){
            return(getContainerForPbr());
        }
        else if(shaderMode.equals(TerrainShaderMode.ADVANCED_PBR)){
            return(getContainerForAdvancedPbr());
        }
        else{
            return null;
        }
        
    
    }
    
    public Container getContainerForPhong(){
        
         slotValuesContainer.clearChildren();
        
        slotValuesContainer.addChild(selectionCheckbox, 0,0);
        
        
          slotValuesContainer.addChild(scaleValueTextField, 0, 1);
         
        slotValuesContainer.addChild(albedoIcon, 0,2);
         slotValuesContainer.addChild(normalMapIcon, 0, 3);
          slotValuesContainer.addChild(parallaxMapIcon, 0, 4);
          
          slotValuesContainer.addChild(afflictionModeValueTextField, 0, 5);
          
          return slotContainer;
        
    }
    
    
    public Container getContainerForPbr(){
        slotValuesContainer.clearChildren();
        
        
         
        slotValuesContainer.addChild(selectionCheckbox, 0,0);
         
        slotValuesContainer.addChild(scaleValueTextField, 0, 1);
        
        slotValuesContainer.addChild(albedoIcon, 0,2);
        
        
         slotValuesContainer.addChild(normalMapIcon, 0, 3);
          slotValuesContainer.addChild(parallaxMapIcon, 0, 4);
          
        //   slotValuesContainer.addChild(packedNormalParallaxMapIcon, 0, 4);
           
           
           slotValuesContainer.addChild(roughnessValueTextField, 0, 6);
           slotValuesContainer.addChild(metallicValueTextField, 0, 7);
        
           slotValuesContainer.addChild(afflictionModeValueTextField, 0, 8);
           
           
           return slotContainer;
           
        
    }
    
    public Container getContainerForAdvancedPbr(){
         slotValuesContainer.clearChildren();
        
         
         slotValuesContainer.addChild(selectionCheckbox, 0,0);
        
         slotValuesContainer.addChild(scaleValueTextField, 0, 1);
        
        slotValuesContainer.addChild(albedoIcon, 0,2);
        
        
         slotValuesContainer.addChild(normalMapIcon, 0, 3);
          slotValuesContainer.addChild(parallaxMapIcon, 0, 4);
          
        //   slotValuesContainer.addChild(packedNormalParallaxMapIcon, 0, 4);
           
           slotValuesContainer.addChild(roughnessValueTextField, 0, 6);
           slotValuesContainer.addChild(roughnessMapIcon, 0, 7);
           
           slotValuesContainer.addChild(metallicValueTextField, 0, 8);
           slotValuesContainer.addChild(metallicMapIcon, 0, 9);
           
           slotValuesContainer.addChild(aoMapIcon, 0, 10);
           slotValuesContainer.addChild(eiMapIcon, 0, 11);
           
           slotValuesContainer.addChild(afflictionModeValueTextField, 0, 12);
        
           
           return slotContainer;
        
    }
    

    private final Container albedoIcon, normalMapIcon, parallaxMapIcon, aoMapIcon, roughnessMapIcon, metallicMapIcon, eiMapIcon, 
            packedNormalParallaxMapIcon, packedMetallicRoughnessAoEiMapIcon;
    
    
    private TextField scaleValueTextField, roughnessValueTextField, metallicValueTextField, afflictionModeValueTextField;
    
        
    public void setAlbedoTexArrayId(int albedoTexArrayId) {  this.albedoTexArrayId = albedoTexArrayId;  }
    public void setPackedNormalParallaxTexArrayId(int packedNormalParallaxTexArrayId) {   this.packedNormalParallaxTexArrayId = packedNormalParallaxTexArrayId;  }
    public void setPackedMetallicRoughnessAoEitextureId(int packedMetallicRoughnessAoEitextureId) {   this.packedMetallicRoughnessAoEiTexArrayId = packedMetallicRoughnessAoEitextureId;   }

    public void setAlbedoTexture(Texture albedoTexture) {
        this.albedoTexture = albedoTexture;         
        
        if(albedoTexture != null){
            albedoIcon.setBackground(new QuadBackgroundComponent(albedoTexture));
            albedoAssetKey = albedoTexture.getKey().getName();
        }
        
    }
    
    public void setNormalMapTexture(Texture normalMapTexture) {
        this.normalMapTexture = normalMapTexture;
         
         
         if(normalMapTexture != null){
            normalMapIcon.setBackground(new QuadBackgroundComponent(normalMapTexture));
            normalMapAssetKey = normalMapTexture.getKey().getName();
        }
    }
    

    public void setParallaxTexture(Texture parallaxTexture) {
        this.parallaxTexture = parallaxTexture;
        
        
        if(parallaxTexture != null){
            parallaxMapIcon.setBackground(new QuadBackgroundComponent(parallaxTexture));
            parallaxAssetKey = parallaxTexture.getKey().getName();
        }
    
    }
    
       
    public void setPackedNormalParallaxTexture(Texture packedNormalParallaxTexture) {
        this.packedNormalParallaxTexture = packedNormalParallaxTexture;
        
        if(packedNormalParallaxTexture != null){
            packedNormalParallaxMapIcon.setBackground(new QuadBackgroundComponent(packedNormalParallaxTexture));
            packedNormalParallaxAssetKey = packedNormalParallaxTexture.getKey().getName();
        }
   
    }
    
    
    public void setMetallicTexture(Texture metallicTexture) {
        this.metallicTexture = metallicTexture;
                
        if(metallicTexture != null){
            metallicMapIcon.setBackground(new QuadBackgroundComponent(metallicTexture));
            metallicAssetKey = metallicTexture.getKey().getName();
        }
        
    }

    public void setRoughnessTexture(Texture roughnessTexture) {
        this.roughnessTexture = roughnessTexture;
                
        if(roughnessTexture != null){
            roughnessMapIcon.setBackground(new QuadBackgroundComponent(roughnessTexture));
            roughnessAssetKey = roughnessTexture.getKey().getName();
        }
    }
    
    public void setAmbientOcclusionTexture(Texture ambientOcclusionTexture) {
        this.ambientOcclusionTexture = ambientOcclusionTexture;
        
         
         if(ambientOcclusionTexture != null){
            aoMapIcon.setBackground(new QuadBackgroundComponent(ambientOcclusionTexture));
            ambientOcclusionAssetKey = ambientOcclusionTexture.getKey().getName();
        }
    }

    public void setEmissiveIntensityTexture(Texture emissiveIntensityTexture) {
        this.emissiveIntensityTexture = emissiveIntensityTexture;
         
         
         if(emissiveIntensityTexture != null){
             eiMapIcon.setBackground(new QuadBackgroundComponent(emissiveIntensityTexture));
            emissiveIntensityAssetKey = emissiveIntensityTexture.getKey().getName();
        }
    }

        
    
    public void setPackedMetallicRoughnessAoEiTexture(Texture packedMetallicRoughnessAoEiTexture) {
        this.packedMetallicRoughnessAoEiTexture = packedMetallicRoughnessAoEiTexture;
         packedMetallicRoughnessAoEiMapIcon.setBackground(new QuadBackgroundComponent(packedMetallicRoughnessAoEiTexture));
         
         if(packedMetallicRoughnessAoEiTexture != null){
            packedMetallicRoughnessAoEiAssetKey = packedMetallicRoughnessAoEiTexture.getKey().getName();
        }
    }


    public void setMetallicValue(float metallicValue) {
        this.metallicValue = metallicValue;
        metallicValueTextField.setText("" + metallicValue);
    }
    
    public void setAdvancedRoughnessValue(float advancedRoughnessValue) {
        this.advancedRoughnessValue = advancedRoughnessValue;
        roughnessValueTextField.setText("" + advancedRoughnessValue);
    }

    public void setAdvancedMetallicValue(float advancedMetallicValue) {
        this.advancedMetallicValue = advancedMetallicValue;        
        metallicValueTextField.setText("" + advancedMetallicValue);
    }




    public void setScale(float scale) {
        this.scale = scale;
        scaleValueTextField.setText("" + scale);
    }
    

    public void setRoughnessValue(float roughnessValue) {
        this.roughnessValue = roughnessValue;
         roughnessValueTextField.setText("" + roughnessValue);
    }



 

    public void setAfflictionMode(int afflictionMode) {
        this.afflictionMode = afflictionMode;
        afflictionModeValueTextField.setText("" + afflictionMode);
    }

    
    
    
    
    public Texture getAlbedoTexture() {
        return albedoTexture;
    }
    public Texture getNormalMapTexture() {
        return normalMapTexture;
    }
    public Texture getParallaxTexture() {
        return parallaxTexture;
    }

    public Texture getAmbientOcclusionTexture() {
        return ambientOcclusionTexture;
    }

    public Texture getMetallicTexture() {
        return metallicTexture;
    }

    public Texture getEmissiveIntensityTexture() {
        return emissiveIntensityTexture;
    }
    
    public float getScale() {
        return scale;
    }

    public Texture getRoughnessTexture() {
        return roughnessTexture;
    }

    public int getPackedNormalParallaxTexArrayId() {
        return packedNormalParallaxTexArrayId;
    }

    public int getAlbedoTexArrayId() {
        return albedoTexArrayId;
    }

    public int getPackedMetallicRoughnessAoEitextureId() {
        return packedMetallicRoughnessAoEiTexArrayId;
    }

    public Texture getPackedNormalParallaxTexture() {
        return packedNormalParallaxTexture;
    }

    public Texture getPackedMetallicRoughnessAoEiTexture() {
        return packedMetallicRoughnessAoEiTexture;
    }

    
    public float getAdvancedRoughnessValue() {        return advancedRoughnessValue;    }
    public float getAdvancedMetallicValue() {        return advancedMetallicValue;    }
    public float getMetallicValue() {        return metallicValue;    }
    public float getRoughnessValue() {        return roughnessValue;    }
    public int getAfflictionMode() {        return afflictionMode;    }
    public TextField getAfflictionModeValueTextField() {        return afflictionModeValueTextField;    }
    public Container getAlbedoIcon() {        return albedoIcon;    }

    public Container getAoMapIcon() {        return aoMapIcon;    }
    public Container getEiMapIcon() {
        return eiMapIcon;
    }

    public Container getMetallicMapIcon() {
        return metallicMapIcon;
    }

    public TextField getMetallicValueTextField() {
        return metallicValueTextField;
    }

    public Container getNormalMapIcon() {
        return normalMapIcon;
    }

    public Container getPackedMetallicRoughnessAoEiMapIcon() {
        return packedMetallicRoughnessAoEiMapIcon;
    }

    public Container getPackedNormalParallaxMapIcon() {
        return packedNormalParallaxMapIcon;
    }

    public Container getRoughnesMapIcon() {
        return roughnessMapIcon;
    }

    public TextField getRoughnessValueTextField() {
        return roughnessValueTextField;
    }

 
    
    
    public Container getParallaxMapIcon() {
        return parallaxMapIcon;
    }
    

    
    public String getAlphaMapForTerrainInSlot(Terrain terrain){
        
        
        if(terrainsRegisteredToThisSlot.contains(terrain)){
            int textureSlotIndexForTerrain = terrainsRegisteredToThisSlotMap.get(terrain);
                if(textureSlotIndexForTerrain < 4){
                    
                    return "AlphaMap";
               }
                else if(textureSlotIndexForTerrain < 8){
                    return "AlphaMap_1";
               }
                else if(textureSlotIndexForTerrain < 12){
                    return "AlphaMap_2";
               }
         
       }
        
        return null;
    }
    
    public TextureChannel getChannelForTerrainInSlot(Terrain terrain){
        
        if(terrainsRegisteredToThisSlot.contains(terrain)){
            
            int textureSlotIndexForTerrain = terrainsRegisteredToThisSlotMap.get(terrain);
        
             
            if(textureSlotIndexForTerrain % 4 == 0 || textureSlotIndexForTerrain == 0){
                return TextureChannel.R;

            }
            else if(textureSlotIndexForTerrain % 4 == 3){
                return TextureChannel.A;

            }
            else if(textureSlotIndexForTerrain % 4 == 1){
                return TextureChannel.G;

            }
            else if(textureSlotIndexForTerrain % 4 == 2){
                return TextureChannel.B;

            }
        }
     
        return null;
        
    }

    
    


    public void setAlbedoTexArray(TextureArray albedoTexArray) {
        this.albedoTexArray = albedoTexArray;
    }

    public void setNormalParallaxTexArray(TextureArray normalParallaxTexArray) {
        this.normalParallaxTexArray = normalParallaxTexArray;
    }

    public void setMetallicRoughnessAoEiTexArray(TextureArray metallicRoughnessAoEiTexArray) {
        this.metallicRoughnessAoEiTexArray = metallicRoughnessAoEiTexArray;
    }

    public TextureArray getAlbedoTexArray() {
        return albedoTexArray;
    }

    public TextureArray getNormalParallaxTexArray() {
        return normalParallaxTexArray;
    }

    public TextureArray getMetallicRoughnessAoEiTexArray() {
        return metallicRoughnessAoEiTexArray;
    }

 
    
    public void removeThisSlotromTerrain(Terrain terrain){
        
        if(terrainsRegisteredToThisSlot.contains(terrain)){
            int slotIndexForTerrainMat = terrainsRegisteredToThisSlotMap.get(terrain);
            
            Material terrainMat = terrain.getMaterial();
            
            if(shaderMode.equals(TerrainShaderMode.PHONG)){
                
                String diffuseString = "DiffuseMap";
                String normalString = "NormalMap";
                if(slotIndexForTerrainMat > 0){  // 0 index doesn't include 0 in the phong shader var name
                    diffuseString += "_" + slotIndexForTerrainMat;
                    normalString += "_" + slotIndexForTerrainMat;
                }
                
                terrainMat.clearParam(diffuseString);
                terrainMat.clearParam("DiffuseMap_" + slotIndexForTerrainMat+ "_scale");
                terrainMat.clearParam(normalString);
                
                
                
            }else if(shaderMode.equals(TerrainShaderMode.PBR)){
                
                terrainMat.clearParam("AlbedoMap_" + slotIndexForTerrainMat);
                terrainMat.clearParam("AlbedoMap_" + slotIndexForTerrainMat+ "_scale");
                terrainMat.clearParam("NormalMap_" + slotIndexForTerrainMat);
                
                terrainMat.clearParam("Roughness_" + slotIndexForTerrainMat);
                terrainMat.clearParam("Metallic_" + slotIndexForTerrainMat);
                
                
            }
            else if(shaderMode.equals(TerrainShaderMode.ADVANCED_PBR)){
                                
                terrainMat.clearParam("AlbedoMap_" + slotIndexForTerrainMat);
                terrainMat.clearParam("AlbedoMap_" + slotIndexForTerrainMat+ "_scale");
                terrainMat.clearParam("NormalMap_" + slotIndexForTerrainMat);
                
                terrainMat.clearParam("Roughness_" + slotIndexForTerrainMat);
                terrainMat.clearParam("Metallic_" + slotIndexForTerrainMat);
                
                 terrainMat.clearParam("MetallicRoughnessMap_" + slotIndexForTerrainMat);
                
                
            }
            
        }
        
        
    }

    public boolean isPairedToTerrain(Terrain terrain){
        if(terrainsRegisteredToThisSlot.contains(terrain)){
            return true;
            
        }
        else{
            return false;
        }
    }

    public int getSlotIdForTerrain(Terrain terrain) {
        return terrainsRegisteredToThisSlotMap.get(terrain);
    
    }
    
    //called when slots are swapped
    public void updateTerrainSlotId(Terrain terrain, int newSlotIndex){
        terrainsRegisteredToThisSlotMap.put(terrain, newSlotIndex);
        
    }
    
   //test albedo clear method, then copy/paste/replace for every other icon and texture
//    public void clearAlbedoMap(){
//        albedoTexArrayId = -1;
//        albedoTexture = null;
//        albedoIcon.setBackground(new QuadBackgroundComponent());
//        
//    }

    
    public String albedoAssetKey;
    public String normalMapAssetKey;
    public String parallaxAssetKey;
    public String packedNormalParallaxAssetKey;
    public String roughnessAssetKey;
    public String metallicAssetKey;
    public String ambientOcclusionAssetKey;
    public String emissiveIntensityAssetKey;
    public String packedMetallicRoughnessAoEiAssetKey;

    public String getAoAssetKey() {
        return ambientOcclusionAssetKey;
    }

    public String getAlbedoAssetKey() {
        return albedoAssetKey;
    }

    public String getNormalMapAssetKey() {
        return normalMapAssetKey;
    }

    public String getPackedMetallicRoughnessAoEiAssetKey() {
        return packedMetallicRoughnessAoEiAssetKey;
    }

    public String getPackedNormalParallaxAssetKey() {
        return packedNormalParallaxAssetKey;
    }

    public String getParallaxAssetKey() {
        return parallaxAssetKey;
    }

    public String getRoughnessAssetKey() {
        return roughnessAssetKey;
    }

    public String getMetallicAssetKey() {
        return metallicAssetKey;
    }

    public String getEiAssetKey() {
        return emissiveIntensityAssetKey;
    }

    public void setAlbedoAssetKey(String albedoAssetKey) {
        this.albedoAssetKey = albedoAssetKey;
        if(albedoAssetKey == null){
            clearAlbedoTexture();
        }
    }

    public void setNormalMapAssetKey(String normalAssetKey) {
        this.normalMapAssetKey = normalAssetKey;
        if(normalMapAssetKey == null){
            clearNormalMapTexture();
        }
    }

    public void setParallaxAssetKey(String parallaxAssetKey) {
        this.parallaxAssetKey = parallaxAssetKey;
        if(parallaxAssetKey == null){
            clearParallaxTexture();            
        }
    }

    public void setPackedNormalParallaxAssetKey(String packedNormalParallaxAssetKey) {
        this.packedNormalParallaxAssetKey = packedNormalParallaxAssetKey;
        if(packedNormalParallaxAssetKey == null){
            clearPackedNormalParallaxTexture();
        }
    }

    public void setRoughnessAssetKey(String roughnessAssetKey) {
        this.roughnessAssetKey = roughnessAssetKey;
        if(roughnessAssetKey == null){
            clearRoughnessTexture();
        }
    }

    public void setMetallicAssetKey(String metallicAssetKey) {
        this.metallicAssetKey = metallicAssetKey;
        if(metallicAssetKey == null){
            clearMetallicTexture();
        }
    }

    public void setAoAssetKey(String AmbientOcclusionAssetKey) {
        this.ambientOcclusionAssetKey = AmbientOcclusionAssetKey;
        if(ambientOcclusionAssetKey == null){
            clearAoTexture();
        }
    }

    public void setEiAssetKey(String EmissiveIntensityAssetKey) {
        this.emissiveIntensityAssetKey = EmissiveIntensityAssetKey;
        if(emissiveIntensityAssetKey == null){
            clearEiTexture();
        }
    }

    public void setPackedMetallicRoughnessAoEiAssetKey(String packedMetallicRoughnessAoEiTextureKey) {
        this.packedMetallicRoughnessAoEiAssetKey = packedMetallicRoughnessAoEiTextureKey;
        if(packedMetallicRoughnessAoEiAssetKey == null){
            clearPackedMetallicRoughnessAoEiTexture();
        }
    }
    
    public void clearAlbedoTexture(){
        albedoTexArrayId = -1;
        albedoAssetKey = null;
        albedoTexture = null;
        albedoIcon.setBackground(defaultquadBackground.clone());
    }
    
    public void clearNormalMapTexture(){
        normalMapAssetKey = null;
        normalMapTexture = null;
        normalMapIcon.setBackground(defaultquadBackground.clone());
        packNormalParallaxMap();
    }
    public void clearParallaxTexture(){
        parallaxAssetKey = null;
        parallaxTexture = null;
        parallaxMapIcon.setBackground(defaultquadBackground.clone());
        packNormalParallaxMap();
    }
    
    public void clearRoughnessTexture(){
        roughnessAssetKey = null;
        roughnessTexture = null;
        roughnessMapIcon.setBackground(defaultquadBackground.clone());
        packMetallicRoughnessAoEiMap();
    }
    public void clearMetallicTexture(){
        metallicAssetKey = null;
        metallicTexture = null;
        metallicMapIcon.setBackground(defaultquadBackground.clone());
        packMetallicRoughnessAoEiMap();
    }
    public void clearAoTexture(){
        ambientOcclusionAssetKey = null;
        ambientOcclusionTexture = null;
        aoMapIcon.setBackground(defaultquadBackground.clone());
        packMetallicRoughnessAoEiMap();
    }
    public void clearEiTexture(){
        emissiveIntensityAssetKey = null;
        emissiveIntensityTexture = null;
        eiMapIcon.setBackground(defaultquadBackground.clone());
        packMetallicRoughnessAoEiMap();
    }
    
    public void clearPackedNormalParallaxTexture() {
        packedNormalParallaxTexArrayId = -1;
        packedNormalParallaxTexture = null;
        packedNormalParallaxAssetKey = null;
        packedNormalParallaxMapIcon.setBackground(null);
    }
    
    public void clearPackedMetallicRoughnessAoEiTexture() {
        packedMetallicRoughnessAoEiTexArrayId = -1;
        packedMetallicRoughnessAoEiTexture = null;
        packedMetallicRoughnessAoEiAssetKey = null;
        packedMetallicRoughnessAoEiMapIcon.setBackground(null);
    }
    

    
     private Texture attemptToLoadTexture(String assetKey){
        Texture texture = null;
        
         if(assetKey != null){
            if(assetKey.length() > 0){
                try{  
                    texture = app.getAssetManager().loadTexture(assetKey);
                    texture.setWrap(Texture.WrapMode.Repeat);
                }
                catch(Exception e){
              //     if(e instanceof AssetNotFoundException){
                       // eventually get rid of println and isntead send an error message at top of screen (or somewhere noticable) indicating that a texture was not found for a slot
                       System.out.println(" ERROR LOADING TEXTURE ASSET:"  + assetKey);
                       System.out.println(e);
                 //      e.printStackTrace();
                //    }
                 }            
            }
         }
          
                
        return texture;
        
    }
     
     
     
     public void reloadTextures(){
         
         albedoTexture = attemptToLoadTexture(albedoAssetKey);
         
         
         normalMapTexture = attemptToLoadTexture(normalMapAssetKey);
         parallaxTexture = attemptToLoadTexture(parallaxAssetKey);
         packedNormalParallaxTexture = attemptToLoadTexture(packedNormalParallaxAssetKey);
         roughnessTexture = attemptToLoadTexture(roughnessAssetKey);
         metallicTexture = attemptToLoadTexture(metallicAssetKey);
         ambientOcclusionTexture = attemptToLoadTexture(ambientOcclusionAssetKey);
         emissiveIntensityTexture = attemptToLoadTexture(emissiveIntensityAssetKey);
         packedMetallicRoughnessAoEiTexture = attemptToLoadTexture(packedMetallicRoughnessAoEiAssetKey);
             
          if(albedoTexture != null){
             this.setAlbedoTexture(albedoTexture); 
          }
          if(normalMapTexture != null){
            this.setNormalMapTexture(normalMapTexture);
          }
         
          if(parallaxTexture != null){
              this.setParallaxTexture(parallaxTexture);
          }
          
          if(packedNormalParallaxTexture != null){
              this.setPackedNormalParallaxTexture(packedNormalParallaxTexture);
          }
          
          if(roughnessTexture != null){
              this.setRoughnessTexture(roughnessTexture);
          }
          
          if(metallicTexture != null){
              this.setMetallicTexture(metallicTexture);
          }
          
          if(ambientOcclusionTexture != null){
              this.setAmbientOcclusionTexture(ambientOcclusionTexture);
          }
          
          if(emissiveIntensityTexture != null){
              this.setEmissiveIntensityTexture(emissiveIntensityTexture);
          }
          
          if(packedMetallicRoughnessAoEiTexture != null){
              this.setPackedMetallicRoughnessAoEiTexture(packedMetallicRoughnessAoEiTexture);
          }
          
          
     }

    public void setName(String newName) { 
        name = newName; 
        
        
        //do not change the UD for terrains registered to this slot. the old name is still used as the item key,
        // and only a display name can be changed (in order to avoid discrepencies with terrains from other maps
        // that are not loaded and cannot have their user data changed to match the slot's new name! )
    
    }

    public String displayName;
    public void setDisplayName(String dName) { 
        displayName = dName; 
        slotNameLabel.setText(displayName);
    }

    public void setShaderMode(TerrainShaderMode newMode) {
        
        
        shaderMode = newMode; 
        
        app.enqueue(() ->{
            if(TerrainShaderMode.ADVANCED_PBR.equals(shaderMode)){
            this.roughnessValueTextField.setText("" + advancedRoughnessValue);
            this.metallicValueTextField.setText("" + advancedMetallicValue);
            
            }else if(TerrainShaderMode.PBR.equals(shaderMode)){           
                this.roughnessValueTextField.setText("" + roughnessValue);
                this.metallicValueTextField.setText("" + metallicValue);

            }
        });
        
        
    }

    
    private void packNormalParallaxMap() {
        String fixedName = getNameWithoutSlashes(name);
        String packedTexFilePath = "/Textures/Terrain_PackedTextures/PackedNormalParallax/" + fixedName + "_PackedNormalParallaxMap.png";
         
        if(normalMapTexture != null || parallaxTexture != null){
             try {
                packedNormalParallaxTexture = textureSlotManager.getTexturePacker().packNormalParallaxMap(normalMapTexture, parallaxTexture, packedTexFilePath, ColorSpace.Linear, null);
                packedNormalParallaxTexture.setKey(new TextureKey(packedTexFilePath));
                this.setPackedNormalParallaxTexture(packedNormalParallaxTexture);
            } catch (IOException ex) {
                Logger.getLogger(TerrainTextureSlot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            clearPackedNormalParallaxTexture();
        }
       
    }   
    
    private void packMetallicRoughnessAoEiMap() {
        
        String fixedName = getNameWithoutSlashes(name);
        String packedMRAoEiTexFilePath = "/Textures/Terrain_PackedTextures/PackedMetallicRoughness/" + fixedName + "_PackedMetallicRoughnessMap.png";
       
       if(metallicTexture != null || roughnessTexture != null || ambientOcclusionTexture != null || emissiveIntensityTexture != null){   
            try {
                packedMetallicRoughnessAoEiTexture = textureSlotManager.getTexturePacker().packMetallicRoughnessAoEmissiveMap(metallicTexture, roughnessTexture, ambientOcclusionTexture, emissiveIntensityTexture, packedMRAoEiTexFilePath, ColorSpace.Linear, null);
                packedMetallicRoughnessAoEiTexture.setKey(new TextureKey(packedMRAoEiTexFilePath));
                this.setPackedMetallicRoughnessAoEiTexture(packedMetallicRoughnessAoEiTexture);
            } catch (IOException ex) {
                Logger.getLogger(TerrainTextureSlot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            clearPackedMetallicRoughnessAoEiTexture();
        }
    }

//returns a version of the name where any / chars are repalced with a - instead.
    // this is necissary when a name that is the same as an asset path needs reused to save another file,
     // in order to prevent extra directories from being made when a name contains slashes..
    private String getNameWithoutSlashes(String string){
        
        String newString = string + "";
        char slash = '/';
        char dash = '-';
        newString = newString.replace(slash, dash);
        
//        for(int s = 0; s < string.length(); s++){
//            if(newString.charAt(s) == slash){
//               
//            }
//        }
        
        return newString;
    }

    public void packTextures() {
        if(normalMapTexture != null || parallaxTexture != null){
            packNormalParallaxMap();
        }
        if(roughnessTexture != null || metallicTexture != null || ambientOcclusionTexture != null || emissiveIntensityTexture != null){
           packMetallicRoughnessAoEiMap();
        }
       
         
        
    }
    
    
}
