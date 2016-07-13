package de.symeda.sormas.ui.utils;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.I18nProperties;

@SuppressWarnings("serial")
public abstract class AbstractEditForm <DTO extends DataTransferObject> extends CustomLayout implements DtoEditForm<DTO> {

	private final BeanFieldGroup<DTO> fieldGroup;
	
	private final String propertyI18nPrefix;	
	
	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix) {
		
		this.propertyI18nPrefix = propertyI18nPrefix;
		
		fieldGroup = new BeanFieldGroup<DTO>(type) {

			@Override
			protected void configureField(Field<?> field) {
				field.setBuffered(isBuffered());

				field.setEnabled(isEnabled());

				if (field.getPropertyDataSource().isReadOnly()) {
					field.setReadOnly(true);
				} else if (isReadOnly()) {
					field.setReadOnly(true);
				}
			}
		};
		
		fieldGroup.setFieldFactory(new DefaultFieldGroupFieldFactory() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
				T field = super.createField(type, fieldType);
				if (field != null) {
					return field;
				}
				
				if (AbstractSelect.class.isAssignableFrom(fieldType)) {
					return (T) createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
				}
				
				return null;
			}
			
			@Override
			protected <T extends AbstractTextField> T createAbstractTextField(Class<T> fieldType) {
				T textField = super.createAbstractTextField(fieldType);
				textField.setNullRepresentation("");
				return textField;
			}
		});

		initLayout();
	}
	
	public void initLayout() {
		setLayout();
	    setSizeFull();
		addFields();
	}
	
	protected abstract void setLayout();
	protected abstract void addFields();
	
	
	@Override
	public DTO getDto() {
		BeanItem<DTO> beanItem = getFieldGroup().getItemDataSource();
		if (beanItem == null) {
			return null;
		} else {
			return beanItem.getBean();
		}
	}

	@Override
	public void setDto(DTO dto) {
		BeanFieldGroup<DTO> fieldGroup = getFieldGroup();
		fieldGroup.setItemDataSource(new BeanItem<DTO>(dto));
	}

	@Override
	public BeanFieldGroup<DTO> getFieldGroup() {
		return this.fieldGroup;
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addField(String propertyId, Class<T> fieldType) {
		T field = getFieldGroup().buildAndBind(propertyId, (Object)propertyId, fieldType);

		field.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix(), propertyId, field.getCaption()));
		if (field instanceof AbstractField) {
			AbstractField abstractField = (AbstractField)field;
			abstractField.setDescription(I18nProperties.getFieldDescription(
					getPropertyI18nPrefix(), propertyId, abstractField.getDescription()));
		}
		
		field.setWidth(100, Unit.PERCENTAGE);
        
		addComponent(field, propertyId);
        return field;
	}
	
	protected void setReadOnly(boolean readOnly, String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getFieldGroup().getField(propertyId).setReadOnly(readOnly);
		}
	}
	
	protected void setVisible(boolean visible, String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
	}
	
	protected void setRequired(boolean required, String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getFieldGroup().getField(propertyId).setRequired(required);
		}
	}

	protected void addFieldListener(String propertyId, ValueChangeListener ...listeners) {
		for (ValueChangeListener listener : listeners) {
			getFieldGroup().getField(propertyId).addValueChangeListener(listener);
		}
	}

	protected String getPropertyI18nPrefix() {
		return propertyI18nPrefix;
	}
}
