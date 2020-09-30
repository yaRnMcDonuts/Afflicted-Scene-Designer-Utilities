/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.Lemur;

import com.jme3.math.Vector3f;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.BorderLayout.Position;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 *
 * @author ryan
 */
public class ConfirmationContainer extends Container{
    
    private Button confirmButton;
    private Button cancelButton;
    
    

    public Button getConfirmButton() {  return confirmButton;   }
    public Button getCancelButton() {   return cancelButton;  }
    
    public ConfirmationContainer(String titleText, String confirmText, String cancelText){
        super();
        
        setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Even));
        
        Container titleContainer = new Container();
        Label titleLabel = new Label(titleText);
        BorderLayout titleLayout = new BorderLayout();
        titleLayout.addChild(titleLabel, Position.Center);
        titleContainer.setLayout(titleLayout);
        
        Container buttonsContainer = new Container();
        
        confirmButton = new Button(confirmText);
        cancelButton = new Button(cancelText);
        buttonsContainer.addChild(confirmButton);
        buttonsContainer.addChild(cancelButton, 0, 1);
        
        confirmButton.setPreferredSize(new Vector3f(380, 120, 1));
        cancelButton.setPreferredSize(new Vector3f(380, 120, 1));
        
        cancelButton.setTextHAlignment(HAlignment.Center);
        cancelButton.setTextVAlignment(VAlignment.Center);
        confirmButton.setTextHAlignment(HAlignment.Center);
        confirmButton.setTextVAlignment(VAlignment.Center);
        
        confirmButton.setInsets(new Insets3f(26,30,26,30));
        cancelButton.setInsets(new Insets3f(26,30,26,30));
        
        confirmButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {            
           close();             
        });
        
         cancelButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {            
          close(); 
        });
        
        addChild(titleContainer);                
        addChild(buttonsContainer);
        
    }
    
    public void close(){
        if(getParent() != null){
            getParent().detachChild(this);
        }
    }
    
    
}
