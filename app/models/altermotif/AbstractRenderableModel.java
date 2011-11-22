package models.altermotif;

import play.mvc.Scope.RenderArgs;

/**
 * A model objet that is able to put himself inside the render arguments provided to the view
 * 
 * @author Svend
 *
 */
public abstract class AbstractRenderableModel {

	public AbstractRenderableModel() {
		super();
	}
	
	protected abstract String getRenderParamName();
	
	public void putInArgsList(RenderArgs renderArgs) {
		renderArgs.put(getRenderParamName(), this);
	}
	

}
