/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASD_Utilities.Lemur;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 *
 * @author ryan
 */
public class TitleContainer extends Container{
    
    //a container that has a title bar along the top with an x button for easily closing out
    
    private Button closeButton;
    private Label titleLabel;
    public Container contentContainer;
    
    public Button getCloseButtton(){ return closeButton; }
    public Container getContentContainer() {  return contentContainer; }
    public Label getTitleLabel() {  return titleLabel; }
    
    public TitleContainer(String title, boolean showCloseButton, Container contentContainer){
        super();
        this.contentContainer = contentContainer;
        
        setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Proportional));
        
        Container closeContainer = new Container();
        
        
        BorderLayout border = new BorderLayout();
        
        titleLabel = new Label(title);
        
      
        
        closeButton = new Button(" X");
        
        closeButton.setPreferredSize(new Vector3f(24,24,1));
        
        
        if(showCloseButton){
            border.addChild(BorderLayout.Position.East, closeButton);
            
        
        
        }
        
        
        
        
        border.addChild(BorderLayout.Position.Center, titleLabel);
        
        closeContainer.setLayout(border);
        
        
       super.addChild(closeContainer);
       
       if(contentContainer == null){
           contentContainer = new Container();
           
       }
         super.addChild(contentContainer);
        
    }
    
    public void clearCloseButtonActions(){
     
    }
    
    
    @Override
    public <T extends Node> T addChild( T child, Object... constraints ) {
        contentContainer.getLayout().addChild(child, constraints);
        return child;
    }

    public void autoClose() {
        closeButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {
            if(getParent() != null){
                getParent().detachChild(this);
            }
        });
    }

    public void setContentContainer(Container container) {
        super.removeChild(container);
        
        contentContainer = container;
         super.addChild(contentContainer);
    }
    
    
    
}
