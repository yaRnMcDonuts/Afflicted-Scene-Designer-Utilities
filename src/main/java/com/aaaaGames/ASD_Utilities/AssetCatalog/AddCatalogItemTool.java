/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog;


import ASD_Utilities.Lemur.ConfirmationContainer;
import ASD_Utilities.Lemur.TitleContainer;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedReference;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

/**
 *
 * @author ryan
 */
public class AddCatalogItemTool {
    
    public CatalogManager catalogManager;
    
    public TitleContainer titleContainer;
    public Container windowContainer;
    
    public SimpleApplication app;
    
    public Button closeButton;
    
    public boolean isOpen = false;
    public boolean isOpen(){ return isOpen; }
    
    public TextField itemAssetPathTextField;    
    public TextField itemNameTextField;
    
    private Button deleteItemButton;
    private ConfirmationContainer confirmDeleteContainer;
    
    private VersionedReference materialVariantsVersionedReference, assetTagsVersionedReference;
    
    public Label itemInputErrorLabel;
    
    public String assetDefaultPath;
    
    public float screenHeight, screenWidth;
    
    public AddCatalogItemTool(CatalogManager catalogManager){
        this.catalogManager = catalogManager;
        app = catalogManager.getApp();
        
        
        assetDefaultPath = "Models/";
        
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        screenWidth = (float) screenSize.getWidth() * .99f;        
        screenHeight = (float) screenSize.getHeight()  * .925f;
        
        windowContainer = new Container();        
        titleContainer = new TitleContainer("New Catalog Item", true, windowContainer);
        titleContainer.move(new Vector3f(screenWidth * 0.4f, screenHeight * 0.7f, 0));
         
        closeButton = titleContainer.getCloseButtton();        
        closeButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {            
            close();        
        });
        
        deleteItemButton = new Button("Delete");
        deleteItemButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {            
           showDeleteCatalogItemConfirmation();            
        });
        
        
        
      
        
        confirmDeleteContainer = new ConfirmationContainer("Confirm Deletion", "Delete", "Cancel");        
        confirmDeleteContainer.setLocalTranslation(new Vector3f(0.45f * screenWidth, 0.6f * screenHeight, 1));
     //   confirmDeleteContainer.setPreferredSize(catalogManager.getAppController().getPercentageVector(0.3f, 0.2f, 1));
        
        confirmDeleteContainer.getConfirmButton().addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {            
            deleteSelectedCatalogItem();
            close();        //go back to catalog
        });
        confirmDeleteContainer.getCancelButton().addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {            
            app.getGuiNode().attachChild(titleContainer); //re-show the edit/add item tool window
        });
        
        
        makeInterface();
      
        
    }
    
    
    
    //override if for unique catalog items that need a different interface (i.e. terrainTextureSlotCatalogItmes)
    public void makeInterface(){
        
        Container itemAssetPathContainer = getItemAssetPathContainer();                     
        windowContainer.addChild(itemAssetPathContainer, 1, 0);                
        
        Container nameContainer = getNameContainer();
        windowContainer.addChild(nameContainer, 2, 0);       
                
        Container tagsContainer = makeTagButtonsContainer();            
        windowContainer.addChild(tagsContainer, 3, 0);

        Container materialVariantsContainer = getMaterialVariantsContainer();        
        windowContainer.addChild(materialVariantsContainer, 4, 0);
        
        //label for error reporting, if the asset isnt able to be added and whatnot
        Container errorContainer = getErrorContainer();
        windowContainer.addChild(errorContainer, 5, 0);   
        
    }

    
    public Button makeTagButton(AssetTag tag){
        Button tagButton = new Button(tag.getString());
        
         if(!currentTags.contains(tag)){
            currentTags.add(tag);
        }
        
        tagButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            
            
            tagButton.getParent().detachChild(tagButton);
            currentTags.remove(tag);
            newTags.remove(tag);
            
        });
        
        return tagButton;
    }
    
    
    public void open(){
         app.getGuiNode().attachChild(titleContainer);
         isOpen = true;
         
         newTagTextField.setText("");
         itemAssetPathTextField.setText(assetDefaultPath);
         currentTagsContainer.detachAllChildren();
         
         if(selectedItemToEdit != null){             
             windowContainer.addChild(deleteItemButton);
         }else{
             windowContainer.removeChild(deleteItemButton);
         }
        
    }
    
    
    
    public void close(){
                      selectedItemToEdit = null; // clear the item being edited on close

                app.enqueue(() ->{
                        app.getGuiNode().detachChild(titleContainer);
                    
               });
                        isOpen = false;


                        for(int t = 0; t <  newTags.size(); t++){
                            catalogManager.unregisterTag(newTags.get(t));
                        }
                        newTags.clear();

        
                
                
                        
        
        
        
    }
    

    

    
    public CatalogItem selectedItemToEdit = null;
    
    public void editExitingCatalogItem(CatalogItem item){
        selectedItemToEdit = item;
        
        open();
        
        
        
        itemNameTextField.setText(selectedItemToEdit.getDisplayName());
        itemAssetPathTextField.setText(selectedItemToEdit.getAssetKeyString());
        
        
        ArrayList<AssetTag> itemToEditTags = selectedItemToEdit.getAssetTagsList();
        
        for(int i = 0; i< itemToEditTags.size(); i++){
            currentTagsContainer.addChild(makeTagButton(itemToEditTags.get(i)));
        }
        
        ArrayList<String> itemToEditMatVars = selectedItemToEdit.getMaterialVariants();
        for(int i = 0; i< itemToEditMatVars.size(); i++){
            String matVarString = itemToEditMatVars.get(i);
            if(matVarString.length() > 0){
                currentMaterialVariantsContainer.addChild(makeMaterialVariantButton(matVarString));
            }
            
        }
        
    }
    
    
    public void createNewCatalogItem(){
        
        String newItemName = itemNameTextField.getText();
        String newItemAssetPath = itemAssetPathTextField.getText();
        
        
         
        //checks to see if the name and asset path are valid (i.e. double check that the asset loads and doesnt throw an exception for not existing!)
        
        
        String inputErrorString = null;
        
        
        
        if(newItemName.equalsIgnoreCase("[Name]")){
            
            inputErrorString = "Asset requires a valid name.";
        }
        else if(newItemName.length() < 2){
             inputErrorString = "Name bust be at least 2 characters long.";
        }
        else{
            
            try{
                Spatial testLoadSpatial = app.getAssetManager().loadModel(newItemAssetPath);
            }
            catch(AssetNotFoundException e){
                inputErrorString = "Invalid Key - Model Not Found.";
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println(e);
                inputErrorString = e.toString();
            }
            
            
        }
        
        
        
        
            
        
          
        if(inputErrorString == null){
            
            
           if(selectedItemToEdit == null){ //make new item if not editing
                CatalogItem newItem = new CatalogItem(newItemName, newItemAssetPath, catalogManager.getModelCatalog());

                selectedItemToEdit = newItem;
                
                
                   catalogManager.addItemToCatalog(newItem, catalogManager.getActiveCatalog());
                   
            }
            else if (selectedItemToEdit != null){ //or instead update the new params into the item that is beind edited
                
                // clear old tags so the new ones can be filled instead
                selectedItemToEdit.clearTags();
                selectedItemToEdit.clearMatVars();
                
                selectedItemToEdit.setAssetPath(newItemAssetPath);
                selectedItemToEdit.setName(newItemName);
                
               
                
                
                //go back to the catalog with this item selected after its been edited
                
                app.enqueue(() ->{
                    close();
                    selectedItemToEdit = null;
                });
                
                
            }
           
            for(int t = 0; t < currentTags.size(); t++){
                AssetTag tagToAdd = currentTags.get(t);
                selectedItemToEdit.addTag(tagToAdd);
            }
             for(int t = 0; t < currentMaterialVariants.size(); t++){
                String matVartoAdd = currentMaterialVariants.get(t);
                selectedItemToEdit.addMaterialVariant(matVartoAdd);
            }
            
         

            itemAssetPathTextField.setText(assetDefaultPath);

            newTagTextField.setText("");
            currentTagsContainer.detachAllChildren();
            
            
            newMaterialVariantTextField.setText("");
            currentMaterialVariantsContainer.detachAllChildren();
            currentMaterialVariants.clear();
            
            itemNameTextField.setText("");
            
            currentTags.clear();
            newTags.clear();
            
            itemInputErrorLabel.setColor(ColorRGBA.Green);
            itemInputErrorLabel.setText("Asset Successfully Added!");
            
            errorFadeTime = 2.2f;
            
             selectedItemToEdit = null;
        }
        else {
            
            itemInputErrorLabel.setColor(ColorRGBA.Orange);
            itemInputErrorLabel.setText("ERROR: " + inputErrorString);
            
            errorFadeTime = 2.2f;
        }
        
        
       
        
       
    }
    
    private Button makeMaterialVariantButton(String variantString){
         Button materialVariantButton = new Button(variantString);
        
        materialVariantButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            
            
            materialVariantButton.getParent().detachChild(materialVariantButton);
            currentMaterialVariants.remove(variantString);
            
        });
        
        return materialVariantButton;
    }
    
    public Container currentMaterialVariantsContainer;
    public TextField newMaterialVariantTextField;
    
    public ArrayList<String> currentMaterialVariants = new ArrayList();
    
    
    public void confirmMaterialVariantInTextField(){
        String materialVariantString = newMaterialVariantTextField.getText();
        if(materialVariantString.length() > 1){
             String lastLetter= materialVariantString.substring(materialVariantString.length() - 1);
        
            if(lastLetter.equals(",") || lastLetter.equals(" ")){

                materialVariantString = materialVariantString.substring(0, materialVariantString.length() - 1);
            }

            if(materialVariantString.length() > 1){
                
                //check to make sure the model and the alt material for all children objects of this model exists prior to setting it as a variant!
                
                String inputErrorString = "";
                try{
                    
                    String newItemAssetPath = itemAssetPathTextField.getText();
                    
                    
                    Node testLoadSpatialNode = (Node) app.getAssetManager().loadModel(newItemAssetPath); // WILL SHOW ERROR INFO MESSAGE IN GUI IF MODEL DOESNT EXIST
                    
                    try{
                        if(checkNodeForMaterialVariant(testLoadSpatialNode, materialVariantString)){
                            
                        }
                        else{
                            inputErrorString = "Some models attached to this asset do not have a material assigned!";
                        }
                        
                    


                        
                        



                    }
                    catch(AssetNotFoundException e){
                        inputErrorString = "Invalid Extension - Material Variant Not Found.";
                    }
                    catch(Exception e){
                        System.out.println(e);
                        e.printStackTrace();
                        inputErrorString = e.toString();
                    }

                    
                    
                }
                catch(AssetNotFoundException e){
                    inputErrorString = "Invalid Key - Model Not Found.";
                }
                catch(Exception e){
                    System.out.println(e);
                    inputErrorString = e.toString();
                }
                
                
               

                if(inputErrorString == null || "".equals(inputErrorString)){
                    if(!currentMaterialVariants.contains(materialVariantString)){  
                        if(materialVariantString.length() > 0){
                                currentMaterialVariantsContainer.addChild(makeMaterialVariantButton(materialVariantString));
                                currentMaterialVariants.add(materialVariantString);
                            }
                        }

                        


                
                        

                  itemInputErrorLabel.setColor(ColorRGBA.Green);
                  itemInputErrorLabel.setText("Material Variant Added!");

                  errorFadeTime = 2.2f;
              }
              else {

                  itemInputErrorLabel.setColor(ColorRGBA.Orange);
                  itemInputErrorLabel.setText("ERROR: " + inputErrorString);

                  errorFadeTime = 2.2f;
              }



                
            }
        }
       
        
       
            newMaterialVariantTextField.setText("");
    }
    
    
    private boolean checkNodeForMaterialVariant(Node nodeToCheck, String materialVariantExtensionString){
        SafeArrayList<Spatial> children = (SafeArrayList<Spatial>) nodeToCheck.getChildren();

        for(int s = 0; s < children.size(); s ++){
            Spatial childSpatial = children.get(s);

            if(childSpatial instanceof Node){
                return checkNodeForMaterialVariant((Node) childSpatial, materialVariantExtensionString);
            }
            else if(childSpatial instanceof Geometry){
                Geometry geoToCheck = (Geometry) childSpatial;
                Material geoMat = geoToCheck.getMaterial();
                if(geoMat != null){
                    
                    String originalMatString = geoMat.getAssetName();
                    originalMatString = originalMatString.substring(0, originalMatString.length() - 4);
                    
                    String materialVariantFullString = originalMatString + "_" + materialVariantExtensionString + ".j3m";
                    
                    
                    
                    Material variantMat = app.getAssetManager().loadMaterial(materialVariantFullString);
                    
                    if(variantMat == null){
                        return false;
                    }
                    
                }
                else if(geoMat == null){
                    return false; 
                }
            }

        }
        
        
        return true;
    }
    
    
    public Container currentTagsContainer;
    public TextField newTagTextField;
    
    public ArrayList<AssetTag> currentTags = new ArrayList();
    
    //store the new tags that are unique to only this item, so they can be removed incase the window is X'd out and the item isnt added with that tag
    public ArrayList<AssetTag> newTags = new ArrayList();
    
    public void confirmTagInTextField(){
        String tagString = newTagTextField.getText();
        if(tagString.length() > 1){
             String lastLetter= tagString.substring(tagString.length() - 1);
        
            if(lastLetter.equals(",") || lastLetter.equals(" ")){

                tagString = tagString.substring(0, tagString.length() - 1);
            }

            if(tagString.length() > 1){
                AssetTag addedTag = catalogManager.getTag(tagString);

                if(addedTag == null){
                    addedTag = catalogManager.registerNewTag(tagString);
                    newTags.add(addedTag);
                }

                if(!currentTags.contains(addedTag)){
                    currentTagsContainer.addChild(makeTagButton(addedTag));
                    
                }

            }
        }
       
        
       
            newTagTextField.setText("");
        
    }
    
    public void bindingPressed(String binding, boolean isPressed, float tpf) {
         
        if(binding.equals("Enter")){
            
            
            if(lastInteractedTextField != null){
                if(lastInteractedTextField.equals(newTagTextField)){
                    confirmTagInTextField();
                    
                }
                else if(lastInteractedTextField.equals(newMaterialVariantTextField)){
                    
                    confirmMaterialVariantInTextField();
                }
            }
            else{
                
            }
            
            
            
            
        }
        
    }
    
    private TextField lastInteractedTextField = null;
    
    public float errorFadeTime = Float.NaN; 
    public void update(float tpf){
        
        if(assetTagsVersionedReference != null){
            if(assetTagsVersionedReference.update()){
                lastInteractedTextField = newTagTextField;
                
                String textString = newTagTextField.getText();
                if(textString.length() > 2){
                    String lastLetter = textString.substring(textString.length() -1);
                    if(lastLetter.equals(",") || lastLetter.equals(" ") || lastLetter.equals("Enter")){
                        confirmTagInTextField();
                        
                    }
                }
                
                
                
            }
        }
        
          if(materialVariantsVersionedReference != null){
            if(materialVariantsVersionedReference.update()){
                lastInteractedTextField = newMaterialVariantTextField;
                
                String textString = newMaterialVariantTextField.getText();
                if(textString.length() > 2){
                    String lastLetter = textString.substring(textString.length() -1);
                    if(lastLetter.equals(",") || lastLetter.equals(" ")){
                        confirmMaterialVariantInTextField();
                        
                    }
                }             
            }
        }
        
        if(errorFadeTime != Float.NaN){
            if(errorFadeTime > 0){
                errorFadeTime -= tpf;
            }
            else{
                itemInputErrorLabel.setText("");
                errorFadeTime = Float.NaN;
            }
        }
    
        
    }

    //interface elements for a default addCatalogItem interface
    
    public Container getMaterialVariantsContainer() {
        Container materialVariantsContainer =  new Container();
        materialVariantsContainer.setInsets(new Insets3f(2, 20, 2, 20));

        Label materialVariantsTitleLabel = new Label("Material Variants: ");

        materialVariantsContainer.addChild(materialVariantsTitleLabel, 0,0);

        Label addVariantLabel = new Label("Add Variant:");
        addVariantLabel.setTextHAlignment(HAlignment.Right);
        materialVariantsContainer.addChild(addVariantLabel, 0,2);

        newMaterialVariantTextField = new TextField("");
        newMaterialVariantTextField.setPreferredWidth( 120);
        materialVariantsContainer.addChild(newMaterialVariantTextField, 0,3);

        materialVariantsVersionedReference = newMaterialVariantTextField.getDocumentModel().createReference();


        currentMaterialVariantsContainer = new Container();
        currentMaterialVariantsContainer.setInsets(new Insets3f(2, 1, 4, 1));
        currentMaterialVariantsContainer.setLayout(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None));
        materialVariantsContainer.addChild(currentMaterialVariantsContainer,1, 0);
        
        return materialVariantsContainer;
    }

    public Container getNameContainer() {
           Container nameContainer = new Container();
        nameContainer.setInsets(new Insets3f(2, 20, 16, 20));
        
        itemNameTextField = new TextField("[Name]");
        itemNameTextField.setPreferredWidth(120);
        
        Label nameTitleLabel = new Label("Asset Name: ");
        
        nameContainer.addChild(nameTitleLabel, 0, 0);
        nameContainer.addChild(itemNameTextField, 0,1);
         
        return nameContainer;
    }
    
        
    public Container getItemAssetPathContainer(){
         Container itemAssetPathContainer = new Container();
        
        itemAssetPathTextField = new TextField(assetDefaultPath);
        itemAssetPathTextField.setPreferredWidth(400);
        
          itemAssetPathContainer.setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.First));
          
        itemAssetPathContainer.setInsets(new Insets3f(20, 10, 8, 4));
        
        itemAssetPathContainer.addChild(itemAssetPathTextField, 0,0);
        
        Button addItemButton = new Button("");
        
        addItemButton.setBackground(new QuadBackgroundComponent(app.getAssetManager().loadTexture("ASD_Assets/Textures/addIconWithBorder.png")));
       
        
        itemAssetPathContainer.addChild(addItemButton, 0,1);
       
         
        
        addItemButton.setPreferredSize(new Vector3f(26, 24, 1));
        addItemButton.setInsets(new Insets3f(0, 2, 0, 0));
        
        addItemButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            
            
            createNewCatalogItem();
            
        });
        return itemAssetPathContainer;
    }
    
    public Container makeTagButtonsContainer(){
           Container tagsContainer = new Container();
        tagsContainer.setInsets(new Insets3f(2, 20, 2, 20));

            Label tagsTitleLabel = new Label("Asset's Tags: ");

            tagsContainer.addChild(tagsTitleLabel, 0,0);
            
            
            Label addTagLabel = new Label("Add Tag:");
            addTagLabel.setTextHAlignment(HAlignment.Right);
            tagsContainer.addChild(addTagLabel, 0,2);

            newTagTextField = new TextField("");
            newTagTextField.setPreferredWidth( 120);
            tagsContainer.addChild(newTagTextField, 0,3);

            assetTagsVersionedReference = newTagTextField.getDocumentModel().createReference();


            currentTagsContainer = new Container();
            currentTagsContainer.setInsets(new Insets3f(2, 1, 4, 1));
                currentTagsContainer.setLayout(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None));
                
                
                

            tagsContainer.addChild(currentTagsContainer,1, 0);
            
            return tagsContainer;
    }

    public Container getErrorContainer() {
        Container errorContainer = new Container();
        BorderLayout borderLayout = new BorderLayout(); 
        errorContainer.setInsets(new Insets3f(10, 8, 4, 8));
    
        
        itemInputErrorLabel = new Label("");
        itemInputErrorLabel.setColor(ColorRGBA.Orange);
        itemInputErrorLabel.setTextHAlignment(HAlignment.Center);
        borderLayout.addChild(BorderLayout.Position.Center, itemInputErrorLabel);
        errorContainer.setLayout(borderLayout);
        
        return errorContainer;
    }

    public void deleteSelectedCatalogItem() {
        
        if(selectedItemToEdit != null){
            
            catalogManager.getActiveCatalog().removeCatalogItem(selectedItemToEdit);
            selectedItemToEdit = null;
        }
        
    }
    
    public void showDeleteCatalogItemConfirmation(){
        app.getGuiNode().attachChild(confirmDeleteContainer);
        
        app.getGuiNode().detachChild(titleContainer);
        
    }
    
    

}
