/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.Lemur;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.event.MouseListener;

/**
 *
 * @author ryan
 */
public class HoverButton extends Button{
    
    private Texture activeTexture, inactiveTexture, hoverTexture, selectedTexture;
    
    private boolean isActive = true;
    public boolean isActive(){ return isActive; }
    
    private boolean isSelected;
    public void setSelected(boolean state){ isSelected = state;  }
    public boolean isSelected(){ return isSelected; }
    
    @Override
    public void setEnabled(boolean b){
        
        super.setEnabled(b);
        isActive = b;
        
        if(!isActive){
            setAlpha(.39f);
            setBackground(new QuadBackgroundComponent(inactiveTexture));
        }else{
            setAlpha(1f);
            setBackground(new QuadBackgroundComponent(activeTexture));
        }
        
        
    }
    
    
    public HoverButton(String s, QuickStyle qStyle) {
        super(s);
        activeTexture = qStyle.getActiveTexture();
        inactiveTexture = qStyle.getInactiveTexture();
        
        hoverTexture = qStyle.getInactiveHoverTexture();
        selectedTexture = qStyle.getActiveHoverTexture();
        
        setTextHAlignment(HAlignment.Center);
        setTextVAlignment(VAlignment.Center);
        
        setBackground(new QuadBackgroundComponent(activeTexture));
        
  //      MouseEventControl.addListenersToSpatial(this, mouseListener);
        
        if(hoverTexture != null){
             addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
                 if(isActive){
                     setBackground(new QuadBackgroundComponent(hoverTexture));
                 }
                
            });
             addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
                  if(!isActive){
                      setBackground(new QuadBackgroundComponent(inactiveTexture));
                  }
                  else{
                      setBackground(new QuadBackgroundComponent(activeTexture));
                  }
                
             });
             
        }
//        else{
//            addCommands(Button.ButtonAction.HighlightOn, (Command<Button>) (Button source) -> {
//                  setAlpha(.98f);
//            });
//             addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
//                  setAlpha(.69f);
//            });
//        }
    }
    
    
//     public MouseListener mouseListener = new DefaultMouseListener() {
//            @Override
//            public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
//                if(event.isReleased() && target != null) {
//                        if(!selected){
//                            on = false;
//                            setText(offString);
//                            setBackground(new QuadBackgroundComponent(offTexture));
//                            addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
//                                setBackground(new QuadBackgroundComponent(offTexture));
//                             });
//                        }
//                        else{
//                            on = true;
//                            setText(onString);
//                            setBackground(new QuadBackgroundComponent(onTexture));
//                            addCommands(Button.ButtonAction.HighlightOff, (Command<Button>) (Button source) -> {
//                                setBackground(new QuadBackgroundComponent(onTexture));
//                             });
//                        }
//                    
//                   
//                }
//            }
//       };
}
