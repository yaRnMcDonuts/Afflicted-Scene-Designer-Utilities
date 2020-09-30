/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.Lemur;


import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.event.MouseListener;

/**
 *
 * @author ryan
 */
public class OnOffButton extends Button{
    
    private boolean on = false;
    public boolean isOn(){ return on; }
    
    private String onString, offString;
    private Texture onTexture, offTexture;
    
    private Texture hoverTexture;
    
    private  QuadBackgroundComponent normalBackground;
    
    public String getState(){ 
        if(on){
            return onString; 
        }
        else{
            return offString;
        }
    }
    
    public OnOffButton(String on, String off){
        super(off);
        
        
        onString = on;
        offString = off;
        
        init();
    }
    
    public OnOffButton(String on, String off, Texture onTex, Texture offTex){
        super(off);
        onString = on;
        offString = off;
        
        onTexture = onTex; 
        offTexture = offTex;
        
        init();
        
        
    }
    
    public OnOffButton(String on, String off, QuickStyle qStyle) {
        super(off); //off by default
        setAlpha(.69f);
        
        onString = on;
        offString = off;
        if(qStyle != null){
            onTexture = qStyle.getActiveTexture();
            offTexture = qStyle.getInactiveTexture();

            hoverTexture = qStyle.getActiveHoverTexture();
        }
      
       init();
        
    }
    
    private void init(){
         if(offTexture != null){
            setBackground(new QuadBackgroundComponent(offTexture));
        }
        else{
          normalBackground = new QuadBackgroundComponent();
          normalBackground.setColor(new ColorRGBA(0.07f, 0.17f, 0.03f, 0.4f));
//            normalBackground.setColor(new ColorRGBA(1, 1, 1, 0f));


            QuadBackgroundComponent border = new QuadBackgroundComponent();
            border.setColor(ColorRGBA.Black);
            border.setMargin(2, 2);
            setBorder(border);
            
            
             setBackground(normalBackground);
             setColor(ColorRGBA.White);
             
        }
        
        
        setTextHAlignment(HAlignment.Center);
        setTextVAlignment(VAlignment.Center);
        
        
        
        MouseEventControl.addListenersToSpatial(this, mouseListener);
        
        if(hoverTexture != null){
             addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                setBackground(new QuadBackgroundComponent(hoverTexture));
            });
             addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                setBackground(new QuadBackgroundComponent(offTexture));
             });
             
        }
        else{
//            addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
//                  setAlpha(.98f);
//            });
//             addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
//                  setAlpha(.69f);
//            });
        }
        
        
    }
    
      public MouseListener mouseListener = new DefaultMouseListener() {
            @Override
            public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
                if(event.isReleased() && target != null) {
                        setOn(!on);
                    
                   
                }
            }
       };

    public void setOn(boolean b) {
        if(on != b){
            on = b;
            if(!on){
                setText(offString);
                
                if(offTexture != null){
                     addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                        setBackground(new QuadBackgroundComponent(offTexture));
                     });
                    setBackground(new QuadBackgroundComponent(offTexture));
                
                }
                else{
                    
                    normalBackground.setColor(new ColorRGBA(0.07f, 0.17f, 0.03f, 0.4f));  
                    
                    setColor(ColorRGBA.White);
                }
                
                
                  
               
            }
            else{
                setText(onString);
                 if(onTexture != null){
                     setBackground(new QuadBackgroundComponent(onTexture));
                    addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                        setBackground(new QuadBackgroundComponent(onTexture));
                     });
                     
                 }
                 else{
                     
                    normalBackground.setColor(new ColorRGBA(0.19f, 0.44f, 0.09f, 0.6f));
                    
                      setColor(new ColorRGBA(1f, 1f, 0.70f, 1.0f));
                     
                 }
            }
        }
    }
}
