/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.AssetCatalog;

/**
 *
 * @author ryan
 */
public class AssetTag {
    
    private String tagName;
    public String getString(){ return tagName;}
    
    public AssetTag(String tag){
        tagName = tag.toLowerCase(); //all tags are lower case to prevent discprenecnies or doubles, and essentialy in order to make sure they can be compared with .equals()
        
    }
    
}
