package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.immunization.components.filter.ImmunizationFilterForm;
import de.symeda.sormas.ui.immunization.components.filter.status.StatusBar;
import de.symeda.sormas.ui.immunization.components.grid.ImmunizationGrid;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class ImmunizationsView extends AbstractView {

	public static final String VIEW_NAME = "immunizations";

	private final ImmunizationCriteria criteria;

	private final StatusBar statusBar;
	private final ImmunizationGrid grid;

	private ImmunizationFilterForm filterForm;

	public ImmunizationsView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(ImmunizationsView.class).get(ImmunizationCriteria.class);
		grid = new ImmunizationGrid(criteria);

		statusBar = new StatusBar(Captions.all, e -> navigateTo(criteria));

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(statusBar);
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser != null && currentUser.hasUserRight(UserRight.IMMUNIZATION_CREATE)) {
			final ExpandableButton createButton =
				new ExpandableButton(Captions.immunizationNewImmunization).expand(e -> ControllerProvider.getImmunizationController().create());
			addHeaderComponent(createButton);
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
	}

	private void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		statusBar.updateStatusButton(String.valueOf(grid.getItemCount()));
		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new ImmunizationFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(ImmunizationsView.class).remove(ImmunizationCriteria.class);
			navigateTo(null, true);
		});

		filterForm.addApplyHandler(clickEvent -> {
			grid.reload();
			statusBar.updateStatusButton(String.valueOf(grid.getItemCount()));
		});
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}
}
