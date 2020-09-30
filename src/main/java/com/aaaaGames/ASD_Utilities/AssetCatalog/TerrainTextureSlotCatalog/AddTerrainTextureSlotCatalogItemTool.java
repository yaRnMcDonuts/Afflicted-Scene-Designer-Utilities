/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog.TerrainTextureSlotCatalog;

import ASD_Utilities.AssetCatalog.AddCatalogItemTool;
import ASD_Utilities.AssetCatalog.AssetTag;
import ASD_Utilities.AssetCatalog.CatalogItem;
import ASD_Utilities.AssetCatalog.CatalogManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Texture;
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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

/**
 *
 * @author ryan
 */
public class AddTerrainTextureSlotCatalogItemTool extends AddCatalogItemTool{
    
    public AddTerrainTextureSlotCatalogItemTool(CatalogManager cm){
        super(cm);
        assetDefaultPath = "Textures/";
        
        
                
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        screenWidth = (float) screenSize.getWidth() * .99f;        
        screenHeight = (float) screenSize.getHeight()  * .925f;
    }
    
    
    private Checkbox normalCheckbox, parallaxCheckbox, metallicCheckbox, roughnessCheckbox, aoCheckbox, eiCheckbox;
    
    private TextField normalTextField, parallaxTextField, metallicTextField, roughnessTextField, aoTextField, eiTextField;
    
    private Container itemEiMapAssetPathContainer, itemAoMapAssetPathContainer, itemRoughnessMapAssetPathContainer, itemMetallicMapAssetPathContainer, itemParallaxMapAssetPathContainer, itemNormalMapAssetPathContainer;
    
    @Override
    public void makeInterface(){
        assetDefaultPath = "Textures/";
        
        Container itemAlbedoMapAssetPathContainer = new Container();
        itemAlbedoMapAssetPathContainer.setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.First));
        Label AlbedoAssetKeyLabel = new Label("Albedo Asset Key");
        itemAssetPathTextField = new TextField(assetDefaultPath);
        itemAssetPathTextField.setPreferredWidth(400);
        itemAlbedoMapAssetPathContainer.addChild(AlbedoAssetKeyLabel);
        itemAlbedoMapAssetPathContainer.addChild(itemAssetPathTextField,1, 0);
         itemAlbedoMapAssetPathContainer.setInsets(new Insets3f(4, 6, 2, 2));
         itemAssetPathTextField.setInsets(new Insets3f(3, 4, 2, 2));
        Button addItemButton = new Button("");
        
        addItemButton.setBackground(new QuadBackgroundComponent(app.getAssetManager().loadTexture("ASD_Assets/Textures/addIconWithBorder.png")));
        
        itemAlbedoMapAssetPathContainer.addChild(addItemButton, 1,1);
       
         
        
        addItemButton.setPreferredSize(new Vector3f(26, 24, 1));
        addItemButton.setInsets(new Insets3f(0, 2, 0, 0));
        
        addItemButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            
            
            createNewCatalogItem();
            
        });
        
        windowContainer.addChild(itemAlbedoMapAssetPathContainer, 1, 0);      
        
        normalCheckbox = new Checkbox("Normal Map");
        normalTextField = new TextField(assetDefaultPath);
        itemNormalMapAssetPathContainer = getItemTextureMapAssetPathContainer( normalCheckbox, normalTextField);        
        
        windowContainer.addChild(itemNormalMapAssetPathContainer, 2, 0);  
        
        
        parallaxCheckbox = new Checkbox("Parallax Map");
        parallaxTextField = new TextField(assetDefaultPath);
        itemParallaxMapAssetPathContainer = getItemTextureMapAssetPathContainer(parallaxCheckbox, parallaxTextField);     
        windowContainer.addChild(itemParallaxMapAssetPathContainer, 3, 0);      
        
         metallicCheckbox = new Checkbox("Metallic Map");
        metallicTextField = new TextField(assetDefaultPath);
        itemMetallicMapAssetPathContainer = getItemTextureMapAssetPathContainer( metallicCheckbox, metallicTextField);     
        windowContainer.addChild(itemMetallicMapAssetPathContainer, 4, 0);      
        
         roughnessCheckbox = new Checkbox("Roughness Map");
        roughnessTextField = new TextField(assetDefaultPath);
        itemRoughnessMapAssetPathContainer = getItemTextureMapAssetPathContainer( roughnessCheckbox, roughnessTextField);     
        windowContainer.addChild(itemRoughnessMapAssetPathContainer, 5, 0);      
        
         aoCheckbox = new Checkbox("Ambient Occlusion Map");
        aoTextField = new TextField(assetDefaultPath);
        itemAoMapAssetPathContainer = getItemTextureMapAssetPathContainer( aoCheckbox, aoTextField);     
        windowContainer.addChild(itemAoMapAssetPathContainer, 6, 0);      
        
         eiCheckbox = new Checkbox("Emissive Intensity Map");
        eiTextField = new TextField(assetDefaultPath);
        itemEiMapAssetPathContainer = getItemTextureMapAssetPathContainer( eiCheckbox, eiTextField);     
        windowContainer.addChild(itemEiMapAssetPathContainer, 7, 0);      
        
        
        Container nameContainer = getNameContainer();
        windowContainer.addChild(nameContainer, 8, 0);       
                
        Container tagsContainer = makeTagButtonsContainer();            
        windowContainer.addChild(tagsContainer, 9, 0);

        
        //label for error reporting, if the asset isnt able to be added and whatnot
        Container errorContainer = getErrorContainer();
        windowContainer.addChild(errorContainer, 10, 0);   
      
      
    }
    
    public Container getItemTextureMapAssetPathContainer(Checkbox checkbox, TextField textField){
        
        Container textureMapAssetPathContainer = new Container();
        
        
        
        textureMapAssetPathContainer.addChild(checkbox);
        
        
        checkbox.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            
            if(checkbox.isChecked()){
                textureMapAssetPathContainer.addChild(textField);
            }else{
                textureMapAssetPathContainer.removeChild(textField);
            }            
            
        });
        
        
    //    itemAssetPathTextField.setPreferredWidth(400);
        
      //    itemAssetPathContainer.setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.First));
          
        textureMapAssetPathContainer.setInsets(new Insets3f(10, 10, 4, 4));
        
        
         textField.setInsets(new Insets3f(3, 4, 2, 2));
        
        
        return textureMapAssetPathContainer;
    }
    
    
    @Override
      public void createNewCatalogItem(){
        
        String newItemName = itemNameTextField.getText();
        
        
        String newItemAlbedoAssetPath = itemAssetPathTextField.getText();
        
        String newItemNormalAssetPath = null;
        String newItemParallaxAssetPath = null;
        String newItemMetallicAssetPath = null;
        String newItemRoughnessAssetPath = null;
        String newItemAoAssetPath = null;
        String newItemEiAssetPath = null;
        
        if(normalCheckbox.isChecked()){
            newItemNormalAssetPath = normalTextField.getText();
        }
        if(parallaxCheckbox.isChecked()){
            newItemParallaxAssetPath = parallaxTextField.getText();
        }
        if(metallicCheckbox.isChecked()){
            newItemMetallicAssetPath = metallicTextField.getText();
        }
        if(roughnessCheckbox.isChecked()){
            newItemRoughnessAssetPath = roughnessTextField.getText();
        }
        if(aoCheckbox.isChecked()){
            newItemAoAssetPath = aoTextField.getText();
        }
        if(eiCheckbox.isChecked()){
            newItemEiAssetPath = eiTextField.getText();
        }
        
         
        //checks to see if the name and asset path are valid (i.e. double check that the asset loads and doesnt throw an exception for not existing!)
        
        TextureSlotManager textureSlotManager = catalogManager.getTextureSlotManager();     

        
        TerrainTextureSlot textureSlot = new TerrainTextureSlot(newItemName, textureSlotManager, screenWidth, screenHeight);              
        
        String inputErrorString = null;
        
        
        
        if(newItemName.equalsIgnoreCase("[Name]")){
            
            inputErrorString = "Asset requires a valid name.";
        }
        else if(newItemName.length() < 2){
             inputErrorString = "Name bust be at least 2 characters long.";
        }
        else{
            String currentAssetPathAttempt = "";
            try{
                itemInputErrorLabel.setColor(ColorRGBA.Yellow);
                
                currentAssetPathAttempt = "Albedo";
                itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                Texture testLoadTexture = app.getAssetManager().loadTexture(newItemAlbedoAssetPath);
                textureSlot.setAlbedoTexture(testLoadTexture);
                
                if(newItemNormalAssetPath != null){
                    currentAssetPathAttempt = "Normal";
                    itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                    testLoadTexture = app.getAssetManager().loadTexture(newItemNormalAssetPath);
                    textureSlot.setNormalMapTexture(testLoadTexture);
                }
                
                if(newItemParallaxAssetPath != null){
                    currentAssetPathAttempt = "Parallax";
                    itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                    testLoadTexture = app.getAssetManager().loadTexture(newItemParallaxAssetPath);
                    textureSlot.setParallaxTexture(testLoadTexture);
                }
                
                if(newItemMetallicAssetPath != null){
                    currentAssetPathAttempt = "Metallic";
                    itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                    testLoadTexture = app.getAssetManager().loadTexture(newItemMetallicAssetPath);
                    textureSlot.setMetallicTexture(testLoadTexture);
                }
                if(newItemRoughnessAssetPath != null){
                    currentAssetPathAttempt = "Roughness";
                    itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                    testLoadTexture = app.getAssetManager().loadTexture(newItemRoughnessAssetPath);
                    textureSlot.setRoughnessTexture(testLoadTexture);
                }
                if(newItemAoAssetPath != null){
                    currentAssetPathAttempt = "Ambeint Occlusion";
                    itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                    testLoadTexture = app.getAssetManager().loadTexture(newItemAoAssetPath);
                    textureSlot.setAmbientOcclusionTexture(testLoadTexture);
                }
                if(newItemEiAssetPath != null){
                    currentAssetPathAttempt = "Emissive Intensity";
                    itemInputErrorLabel.setText("Loading " + currentAssetPathAttempt + " Map...");
                    testLoadTexture = app.getAssetManager().loadTexture(newItemEiAssetPath);
                    textureSlot.setEmissiveIntensityTexture(testLoadTexture);
                }
                
                
                
            }
            catch(AssetNotFoundException e){
                inputErrorString = "Invalid Key - Texture Not Found: " + currentAssetPathAttempt + " Map";
            }
            catch(Exception e){
                System.out.println(e);
                inputErrorString = e.toString();
            }
            
            
        }
        
        
        
        
            
        
          
        if(inputErrorString == null){
            
            
           if(selectedItemToEdit == null){ //make new item if not editing
              
               
                
               textureSlot = textureSlotManager.registerNewTextureSlot(newItemName, textureSlot);  
               
                CatalogItem newItem = catalogManager.getTerrainTextureSlotCatalog().addNewTerrainTextureSlotCatalogItem(textureSlot);
                ((TerrainTextureSlotCatalogItem)newItem).setDisplayName(newItemName);
                
                
                selectedItemToEdit = newItem;
                              
                   
            }
            else if (selectedItemToEdit != null){ //or instead update the new params into the item that is beind edited
                
                // clear old tags so the new ones can be filled instead
                
                TerrainTextureSlot slotToEdit = ((TerrainTextureSlotCatalogItem)selectedItemToEdit).getTerrainTextureSlot();
                
                selectedItemToEdit.clearTags();
                selectedItemToEdit.clearMatVars();
                
                //never change the original name (in order to make sure you dont break references for terrains in other maps), but instead change just the display name
                ((TerrainTextureSlotCatalogItem)selectedItemToEdit).setDisplayName(newItemName);
                                
                slotToEdit.setAlbedoAssetKey(newItemAlbedoAssetPath);
                slotToEdit.setNormalMapAssetKey(newItemNormalAssetPath);
                slotToEdit.setParallaxAssetKey(newItemParallaxAssetPath);
                slotToEdit.setMetallicAssetKey(newItemMetallicAssetPath);
                slotToEdit.setRoughnessAssetKey(newItemRoughnessAssetPath);
                slotToEdit.setAoAssetKey(newItemAoAssetPath);
                slotToEdit.setEiAssetKey(newItemEiAssetPath);
                
               
                slotToEdit.reloadTextures();
               

                itemInputErrorLabel.setText("Packing Textures... ");
                
                
           //     slotToEdit.setName(newItemName);
                slotToEdit.setDisplayName(newItemName);
                
               
                
                
                
                //go back to the catalog with this item selected after its been edited
                
                app.enqueue(() ->{
                     slotToEdit.packTextures();
                     itemInputErrorLabel.setColor(ColorRGBA.Green);
                    itemInputErrorLabel.setText("Asset Successfully Added!");
                    close();
                    selectedItemToEdit = null;

                    ArrayList<Terrain> terrainsUsingEditedSlot = slotToEdit.getTerrainsPairedToSlot();
                    for(int t = 0; t < terrainsUsingEditedSlot.size(); t ++){
                        Terrain pairedTerrain = terrainsUsingEditedSlot.get(t);

                        int slotIndexForTerrain = slotToEdit.getSlotIdForTerrain(pairedTerrain);
                        slotToEdit.setShaderMode(catalogManager.getASDUCore().getTerrainShaderConverter().getShaderModeOfTerrain(pairedTerrain));
                        slotToEdit.mapSlotToShader(pairedTerrain, slotIndexForTerrain); //remap a slot anytime its been edited

                    }
                
                });
                
                
            }
           
            for(int t = 0; t < currentTags.size(); t++){
                AssetTag tagToAdd = currentTags.get(t); 
                selectedItemToEdit.addTag(tagToAdd);
            }
         

            itemAssetPathTextField.setText(assetDefaultPath);
            normalTextField.setText(assetDefaultPath);
            parallaxTextField.setText(assetDefaultPath);
            metallicTextField.setText(assetDefaultPath);
            roughnessTextField.setText(assetDefaultPath);
            aoTextField.setText(assetDefaultPath);
            eiTextField.setText(assetDefaultPath);

            newTagTextField.setText("");
            currentTagsContainer.detachAllChildren();
            
            
            
            itemNameTextField.setText("");
            
            currentTags.clear();
            newTags.clear();
            
            
            
            errorFadeTime = 2.2f;
            
             selectedItemToEdit = null;
        }
        else {
            
            itemInputErrorLabel.setColor(ColorRGBA.Orange);
            itemInputErrorLabel.setText("ERROR: " + inputErrorString);
            
            errorFadeTime = 2.2f;
        }
        
        
       
        
       
    }

      
    @Override
    public void editExitingCatalogItem(CatalogItem item) {
        selectedItemToEdit = item;
        
        open();
        
        TerrainTextureSlotCatalogItem tItem = (TerrainTextureSlotCatalogItem) item;
        
        TerrainTextureSlot slot = tItem.getTerrainTextureSlot();
        
        itemNameTextField.setText(tItem.getDisplayName());
        
        
        itemAssetPathTextField.setText(tItem.getAssetKeyString());
        
        String normalKeyString = slot.getNormalMapAssetKey();
        if(normalKeyString != null && normalKeyString.length() > 0){
            normalTextField.setText(normalKeyString);
            itemNormalMapAssetPathContainer.addChild(normalTextField);
            normalCheckbox.setChecked(true);
        }
        String parallaxKeyString = slot.getParallaxAssetKey();
        if(parallaxKeyString != null && parallaxKeyString.length() > 0){
            parallaxTextField.setText(parallaxKeyString);
            itemParallaxMapAssetPathContainer.addChild(parallaxTextField);
            parallaxCheckbox.setChecked(true);
        }       
        String metallicKeyString = slot.getMetallicAssetKey();
        if(metallicKeyString != null && metallicKeyString.length() > 0){
            metallicTextField.setText(metallicKeyString);
            itemMetallicMapAssetPathContainer.addChild(metallicTextField);
            metallicCheckbox.setChecked(true);
        }
        String roughnessKeyString = slot.getRoughnessAssetKey();
        if(roughnessKeyString != null && roughnessKeyString.length() > 0){
            roughnessTextField.setText(roughnessKeyString);
            itemRoughnessMapAssetPathContainer.addChild(roughnessTextField);
            roughnessCheckbox.setChecked(true);
        }    
        String aoKeyString = slot.getAoAssetKey();
        if(aoKeyString != null && aoKeyString.length() > 0){
            aoTextField.setText(aoKeyString);
            itemAoMapAssetPathContainer.addChild(aoTextField);
            aoCheckbox.setChecked(true);
        }                
        String eiKeyString = slot.getEiAssetKey();
        if(eiKeyString != null && eiKeyString.length() > 0){
            eiTextField.setText(eiKeyString);
            itemEiMapAssetPathContainer.addChild(eiTextField);
            eiCheckbox.setChecked(true);
        }
        
        ArrayList<AssetTag> itemToEditTags = tItem.getAssetTagsList();
        
        for(int i = 0; i< itemToEditTags.size(); i++){
            currentTagsContainer.addChild(makeTagButton(itemToEditTags.get(i)));
        }
        
       
        
    }

    @Override
    public void open() {
        super.open(); 
        
        if(normalTextField.getParent() != null){
            ((Container)normalTextField.getParent()).removeChild(normalTextField);
            normalTextField.setText(assetDefaultPath);
            normalCheckbox.setChecked(false);
        }
        if(parallaxTextField.getParent() != null){
            ((Container)parallaxTextField.getParent()).removeChild(parallaxTextField);
            parallaxTextField.setText(assetDefaultPath);
            parallaxCheckbox.setChecked(false);
        }
        if(metallicTextField.getParent() != null){
            ((Container)metallicTextField.getParent()).removeChild(metallicTextField);
            metallicTextField.setText(assetDefaultPath);
            metallicCheckbox.setChecked(false);
        }
        if(roughnessTextField.getParent() != null){
            ((Container)roughnessTextField.getParent()).removeChild(roughnessTextField);
            roughnessTextField.setText(assetDefaultPath);
            roughnessCheckbox.setChecked(false);
        }
        if(aoTextField.getParent() != null){
            ((Container)aoTextField.getParent()).removeChild(aoTextField);
            aoTextField.setText(assetDefaultPath);
            aoCheckbox.setChecked(false);
        }
        if(eiTextField.getParent() != null){
            ((Container)eiTextField.getParent()).removeChild(eiTextField);
            eiTextField.setText(assetDefaultPath);
            eiCheckbox.setChecked(false);
        }
     
    }
    
    
      
}
