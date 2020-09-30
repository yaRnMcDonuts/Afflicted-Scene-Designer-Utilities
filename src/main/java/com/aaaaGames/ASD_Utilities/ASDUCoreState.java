/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities;

import ASD_Utilities.AssetCatalog.CatalogManager;
import ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter.TerrainShaderConverter;
import ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter.TextureArrayManager;
import ASD_Utilities.TextureUtilities.TextureChannelPacker;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;

/**
 *
 * @author ryan
 */
public class ASDUCoreState extends BaseAppState{
    
    
    public String assetDirectory;
    
    public CatalogManager catalogManager;    
    public TerrainShaderConverter terrainShaderConverter;    
    public TextureArrayManager textureArrayManager;
    public TextureChannelPacker textureChannelPacker;
    
    
    
    public String getAssetDirectory() {        return assetDirectory;    }
    
    public TextureArrayManager getTextureArrayManager() { return textureArrayManager; }
    public CatalogManager getCatalogManager() {        return catalogManager;    }
    public TextureChannelPacker getTextureChannelPacker() {        return textureChannelPacker;    }
    public TerrainShaderConverter getTerrainShaderConverter() {        return terrainShaderConverter;    }
    

    public ASDUCoreState(String assetDir, SimpleApplication app) {
     
        assetDirectory = assetDir;
        
        
        //this order of init matters !
        
          terrainShaderConverter = new TerrainShaderConverter(app, assetDirectory);
          
         textureArrayManager = new TextureArrayManager();         
        textureChannelPacker = new TextureChannelPacker(app, assetDirectory);
        
        catalogManager = new CatalogManager((SimpleApplication) app, assetDirectory, this);
        
        
 
        
      
        terrainShaderConverter.setTextureSlotManager(catalogManager.getTextureSlotManager());
        
        
     
    }
    
    

 

    @Override
    protected void initialize(Application app) {

        
    }

    @Override
    protected void cleanup(Application app) {
        
        catalogManager.cleanUp();
        
      //  textureArrayManager.cleanUp();
    }

    @Override
    protected void onEnable() {
        
        
    }

    @Override
    protected void onDisable() {
        
        
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        
        catalogManager.update(tpf);
        
    } 

    
    
}
