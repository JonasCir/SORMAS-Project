/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class PathogenTestListEntry extends HorizontalLayout {

	private final PathogenTestDto pathogenTest;
	private Button editButton;

	public PathogenTestListEntry(PathogenTestDto pathogenTest) {
		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);
		this.pathogenTest = pathogenTest;

		VerticalLayout labelLayout = new VerticalLayout();
		labelLayout.setSpacing(false);
		labelLayout.setMargin(false);
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		// very hacky: clean up when needed elsewhere!
		HorizontalLayout topLabelLayout = new HorizontalLayout();
		topLabelLayout.setSpacing(false);
		topLabelLayout.setMargin(false);
		topLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(topLabelLayout);
		String htmlTop = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE,
				DataHelper.toStringNullable(pathogenTest.getTestType()))
				+ LayoutUtil.div(DataHelper.toStringNullable(pathogenTest.getTestResultText()));
		Label labelTopLeft = new Label(htmlTop, ContentMode.HTML);
		topLabelLayout.addComponent(labelTopLeft);

		if (pathogenTest.getTestResultVerified()) {
			Label labelTopRight = new Label(VaadinIcons.CHECK_CIRCLE.getHtml(), ContentMode.HTML);
			labelTopRight.setSizeUndefined();
			labelTopRight.addStyleName(CssStyles.LABEL_LARGE);
			labelTopRight.setDescription(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX,
					PathogenTestDto.TEST_RESULT_VERIFIED));
			topLabelLayout.addComponent(labelTopRight);
			topLabelLayout.setComponentAlignment(labelTopRight, Alignment.TOP_RIGHT);
		}

		HorizontalLayout middleLabelLayout = new HorizontalLayout();
		middleLabelLayout.setSpacing(false);
		middleLabelLayout.setMargin(false);
		middleLabelLayout.setWidth(100, Unit.PERCENTAGE);
		labelLayout.addComponent(middleLabelLayout);
		String htmlLeft = LayoutUtil.div(DataHelper.toStringNullable(DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails())));
		Label labelLeft = new Label(htmlLeft, ContentMode.HTML);
		middleLabelLayout.addComponent(labelLeft);

		String htmlRight = LayoutUtil.div(DateHelper.formatLocalShortDateTime(pathogenTest.getTestDateTime()));
		Label labelRight = new Label(htmlRight, ContentMode.HTML);
		labelRight.addStyleName(CssStyles.ALIGN_RIGHT);
		middleLabelLayout.addComponent(labelRight);
		middleLabelLayout.setComponentAlignment(labelRight, Alignment.TOP_RIGHT);

		String htmlBottom = LayoutUtil.divCss(CssStyles.LABEL_BOLD + " " + CssStyles.LABEL_UPPERCASE
				+ " " + (pathogenTest.getTestResult() == PathogenTestResultType.POSITIVE ? CssStyles.LABEL_CRITICAL : 
					(pathogenTest.getTestResult() == PathogenTestResultType.INDETERMINATE ? CssStyles.LABEL_WARNING : "")),
				DataHelper.toStringNullable(pathogenTest.getTestResult()));
		Label labelBottom = new Label(htmlBottom, ContentMode.HTML);
		labelLayout.addComponent(labelBottom);
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = new Button(VaadinIcons.PENCIL);
			CssStyles.style(editButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}
		editButton.addClickListener(editClickListener);
	}

	public PathogenTestDto getPathogenTest() {
		return pathogenTest;
	}
}
