/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.Lemur;

import com.jme3.texture.Texture;

/**
 *
 * @author ryan
 */
public class QuickStyle {
    
    private Texture activeTexture, inactiveTexture, activeHoverTexture, inactiveHoverTexture;

    public Texture getActiveHoverTexture() { return activeHoverTexture; } //aka the selected texture
    public Texture getInactiveTexture() { return inactiveTexture;  } 
    public Texture getInactiveHoverTexture() { return inactiveHoverTexture; }
    public Texture getActiveTexture() {  return activeTexture;  }

    public QuickStyle(Texture activeTex, Texture inactiveTex, Texture activeHoverTex, Texture inactiveHoverTex) {
        
       activeTexture = activeTex;
       inactiveTexture = inactiveTex;
       activeHoverTexture = activeHoverTex;
       inactiveHoverTexture = inactiveHoverTex;
       
    }
    
}
