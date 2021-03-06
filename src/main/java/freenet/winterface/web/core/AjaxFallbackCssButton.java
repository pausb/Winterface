package freenet.winterface.web.core;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

@SuppressWarnings("serial")
public abstract class AjaxFallbackCssButton extends AjaxFallbackLink<String> implements IHeaderContributor {

	/** {@code true} to show icon */
	private boolean showIcon;
	/** Button icon */
	private ButtonIcon icon;

	/** {@link IModel} for content of button label */
	private final IModel<String> labelModel;

	public final static String LABEL_POSTFIX = "-label";

	/** A list of available icons for button */
	public enum ButtonIcon {
		TICK, CANCEL, DELETE, ARROW_UP, ARROW_DOWN, BULLET_ARROW_BOTTOM, BULLET_ARROW_TOP, CROSS, ARROW_OUT, PENCIL, CUT
	}

	/**
	 * Constructs button with no label (only icon)
	 * 
	 * @param id
	 *            {@link Component} ID
	 */
	public AjaxFallbackCssButton(String id) {
		this(id, null);
	}

	/**
	 * Constructs button with label
	 * <p>
	 * If {@link #setIconVisible(boolean)} is set, the button will also render
	 * an icon
	 * </p>
	 * 
	 * @param id
	 *            {@link Component} ID
	 * @param model
	 *            label {@link IModel}
	 */
	public AjaxFallbackCssButton(String id, IModel<String> model) {
		this(id, model, null);
	}

	/**
	 * Constructs button with label and icon
	 * 
	 * @param id
	 *            {@link Component} ID
	 * @param model
	 *            label {@link IModel}
	 * @param icon
	 *            button icon
	 */
	public AjaxFallbackCssButton(String id, IModel<String> model, ButtonIcon icon) {
		super(id);
		this.labelModel = model;
		this.icon = icon == null ? ButtonIcon.TICK : icon;
		this.showIcon = (this.icon != null);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// Customize Css class
		add(new AttributeAppender("class", Model.of("button"), " "));
		// Icon name
		String iconName = icon.toString().toLowerCase();
		// Add label
		Component label = null;
		if (labelModel != null) {
			label = new Label(getId() + LABEL_POSTFIX, labelModel);
			if (showIcon) {
				label.add(new AttributeAppender("class", Model.of("with-icon"), " "));
				label.add(new AttributeAppender("class", Model.of(iconName), " "));
			}
		} else {
			label = createIcon(icon);
		}
		add(label);
	}

	/**
	 * Sets a new icon for button
	 * 
	 * @param icon
	 *            desired icon
	 * @return {@code this}
	 */
	public AjaxFallbackCssButton setIcon(ButtonIcon icon) {
		this.icon = icon;
		return this;
	}

	/**
	 * @return current {@link ButtonIcon}
	 * @see #replaceIcon(ButtonIcon)
	 */
	public ButtonIcon getIcon() {
		return icon;
	}

	/**
	 * Toggles icon visibility
	 * 
	 * @param show
	 *            {@code true} to show button
	 * @return {@code this}
	 */
	public AjaxFallbackCssButton setIconVisible(boolean show) {
		this.showIcon = show;
		return this;
	}

	/**
	 * Replaces the {@link ButtonIcon} if available
	 * 
	 * @param newIcon
	 *            desired icon
	 * @return modified {@code this}
	 */
	public AjaxFallbackCssButton replaceIcon(ButtonIcon newIcon) {
		Component component = this.get(getId() + LABEL_POSTFIX);
		if (component instanceof Image) {
			Image oldImg = (Image) component;
			Image newImg = createIcon(newIcon);
			oldImg.replaceWith(newImg);
		}
		return this;
	}

	/**
	 * Creates an {@link Image} with respect to given {@link ButtonIcon}
	 * <p>
	 * Created {@link Image} is cachable (even over AJAX refreshes)
	 * </p>
	 * 
	 * @param icon
	 * @return created icon
	 */
	private Image createIcon(ButtonIcon icon) {
		return new Image(getId() + LABEL_POSTFIX, getResource("img/" + icon.toString().toLowerCase() + ".png")) {
			@Override
			protected boolean shouldAddAntiCacheParameter() {
				// Buttons icon does not need to be refetched on every AJAX
				// refresh
				return false;
			}
		};
	}

	@Override
	public void renderHead(org.apache.wicket.markup.head.IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(getResource("css-buttons.css")));
	}

	/**
	 * Returns {@link PackageResourceReference} for this component
	 * 
	 * @param name
	 *            name of resource to fetch
	 * @return fetched resource
	 */
	private PackageResourceReference getResource(String name) {
		return new PackageResourceReference(AjaxFallbackCssButton.class, name);
	}

}
