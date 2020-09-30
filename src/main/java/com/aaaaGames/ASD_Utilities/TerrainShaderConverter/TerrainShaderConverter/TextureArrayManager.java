/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.TerrainShaderConverter.TerrainShaderConverter;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ryan
 */
public class TextureArrayManager {

    //texture arrays are identified and selected by the first slot on the terrain. So any terrains that share a textureslot in slot_0 will share a texture array    
    
    public ArrayList<TextureArray> registeredTextureArrays;
    public HashMap<String, TextureArray> textureArraysByNameMap = new HashMap();
    
    public HashMap<String, ArrayList<Image> > textureArrayImageLists = new HashMap();
    
    
    public TextureArrayManager() {
        registeredTextureArrays = new ArrayList();
        
        
    }
    
    public TextureArray getTextureArrayWithName(String texArrayName){        
        return textureArraysByNameMap.get(texArrayName);
      
        
    }
    
    public boolean containsTextureArrayWithName(String texArrayName){
        if(textureArraysByNameMap.get(texArrayName) != null){
            return true;
        }
        else{
            return false;
        }
    }

    
    public TextureArray createAndRegisterNewTextureArray(List<Image> images, String texArrayName){
        
        if(textureArraysByNameMap.get(texArrayName) == null){
            
            TextureArray newTextureArray;
            
            if(images == null){ //create blank holder TextureArray if theres no images so far (useful for normal and mrae map that may not have entries in slot_0 where the name identifiaction comes from)
                newTextureArray = new TextureArray();
                images = new ArrayList();
            }else{
                newTextureArray = new TextureArray(images);
            }
            register(newTextureArray, texArrayName, images);
            
            return newTextureArray;
        }
        
        else{ //return existant texture array if the name is already taken
            return textureArraysByNameMap.get(texArrayName);
        }
        
    }
    
//    //add any new textures to the existing array, if they already aren't contained in there. returns the new full list of all images in the array so that the TextureArray can be reconstructed with updated images
//    public ArrayList<Image> addImagesToArray(TextureArray textureArray, List<Image> imagesToAdd){
//        
//        ArrayList existingImages = textureArrayImageLists.get(textureArray);
//        
//        for(int i= 0; i < imagesToAdd.size(); i ++){
//            
//            Image newImage = imagesToAdd.get(i);
//            
//            if(!existingImages.contains(newImage)){ //add images to list if there are any new ones
//                existingImages.add(newImage);
//            }
//        }
//        
//        return existingImages;
//        
//    }
//         //add any new textures to the existing array, if they already aren't contained in there. returns the index at which the image was contained in the list for indexing in the shader
    public int addImageToArray(TextureArray textureArray, Image imageToAdd){
        
        ArrayList<Image> existingImages = textureArrayImageLists.get(textureArray.getName());
        if(existingImages.contains(imageToAdd)){
            System.out.println( " CONTTTTTTTAIN");
            for(int i= 0; i < existingImages.size(); i ++){
            
                 Image existingImage = existingImages.get(i);
            
                 if(existingImage.equals(imageToAdd)){ //return index that this image is located at in the texArray
                   return i;
                }
            }
        }
        else{
            existingImages.add(imageToAdd);
            
            if(textureArray != null){
                registeredTextureArrays.remove(textureArray);
            }
            else{
               
            }
            //recreate the texture array with new images
            String name = textureArray.getName();
            textureArray = new TextureArray(existingImages);
            register(textureArray, name, existingImages);
            
            return existingImages.size() - 1; //return index of the newly added image at the last index of the array
        }
        
        
        return -1; 
        
    }

    private void register(TextureArray newTextureArray, String texArrayName, List<Image> images) {
        
            registeredTextureArrays.add(newTextureArray);
            
            newTextureArray.setName(texArrayName);
            textureArraysByNameMap.put(texArrayName, newTextureArray);
            
            textureArrayImageLists.put(texArrayName, (ArrayList<Image>) images);
            
            newTextureArray.setWrap(Texture.WrapMode.Repeat);
            newTextureArray.setMinFilter(Texture.MinFilter.Trilinear);
        
        
    }


    
 
    
    
    
}
