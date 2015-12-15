/*******************************************************************************
 * Copyright (c) 2015 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.rdb.core.dialog.table;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.hangum.tadpole.commons.util.GlobalImageUtils;
import com.hangum.tadpole.engine.query.dao.mysql.TableDAO;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.engine.sql.util.dbms.MySQLUtils;
import com.hangum.tadpole.rdb.core.Messages;
import com.hangum.tadpole.rdb.core.dialog.msg.TDBErroDialog;
import com.hangum.tadpole.rdb.core.viewers.object.ExplorerViewer;
import com.hangum.tadpole.rdb.core.viewers.object.sub.utils.TableColumnObjectQuery;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * TableColumn dialog
 * 
 * @author hangum
 *
 */
public class TableColumnDialog extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(TableColumnDialog.class);
	
	private UserDBDAO userDB;
	private TableDAO tableDAO;
	
	private Text textColumnName;
	private Combo comboType;
	private Text textDefault;
	private Button btnPrimaryKey;
	private Button btnNotNull;
	private Button btnAutoIncrement;
	private Combo comboCollation;
	private Text textComment;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TableColumnDialog(Shell parentShell, UserDBDAO userDB, TableDAO tableDAO) {
		super(parentShell);
		setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.MAX | SWT.RESIZE | SWT.TITLE);
		
		this.userDB = userDB;
		this.tableDAO = tableDAO;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Table 컬럼 추가");
		newShell.setImage(GlobalImageUtils.getTadpoleIcon());
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(String.format("%s 컬럼 추가", tableDAO.getName()));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblColumnName = new Label(container, SWT.NONE);
		lblColumnName.setText("Name");
		
		textColumnName = new Text(container, SWT.BORDER);
		textColumnName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblType = new Label(container, SWT.NONE);
		lblType.setText("Type");
		
		comboType = new Combo(container, SWT.NONE);
		comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String strType : ColumnDataTypeDef.getAllTypeNames(userDB.getDBDefine())) {
			comboType.add(strType);
		}
		comboType.setText("VARCHAR(45)");
		
		Label lblPrimaryKey = new Label(container, SWT.NONE);
		lblPrimaryKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		btnPrimaryKey = new Button(composite, SWT.CHECK);
		btnPrimaryKey.setText("Primary Key");
		
		btnNotNull = new Button(composite, SWT.CHECK);
		btnNotNull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textDefault.setText("");
				textDefault.setFocus();
			}
		});
		btnNotNull.setText("Not Null");
		
		btnAutoIncrement = new Button(composite, SWT.CHECK);
		btnAutoIncrement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnPrimaryKey.setSelection(true);
				comboType.setText("INT");
			}
		});
		btnAutoIncrement.setText("Auto Increment");
		
		Label lblDefault = new Label(container, SWT.NONE);
		lblDefault.setText("Default");
		
		textDefault = new Text(container, SWT.BORDER);
		textDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textDefault.setText("NULL");
		
		Label lblCollation = new Label(container, SWT.NONE);
		lblCollation.setText("Collation");
		
		comboCollation = new Combo(container, SWT.NONE);
		comboCollation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(String strCollation : MySQLUtils.getCollation(userDB)) {
			comboCollation.add(strCollation);
		}
		
		Label lblComment = new Label(container, SWT.NONE);
		lblComment.setText("Comment");
		
		textComment = new Text(container, SWT.BORDER | SWT.MULTI);
		GridData gd_textComment = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textComment.heightHint = 90;
		gd_textComment.minimumHeight = 90;
		textComment.setLayoutData(gd_textComment);
		
		initUI();
		
		textColumnName.setFocus();

		return area;
	}
	
	/**
	 * initialize ui
	 */
	private void initUI() {
		
	}
	
	@Override
	protected void okPressed() {
		
		String strName = textColumnName.getText();
		String strType = comboType.getText();
		String strDefault = textDefault.getText();
		boolean isPrimaryKey = btnPrimaryKey.getSelection();
		boolean isNotNull = btnNotNull.getSelection();
		boolean isAutoIncrement = btnAutoIncrement.getSelection();
		String strCollation = comboCollation.getText();
		String strComment = textComment.getText();
		
		TableColumnUpdateDAO metaDataDao = new TableColumnUpdateDAO();
		metaDataDao.setColumnName(strName);
		metaDataDao.setDataType(strType);
		metaDataDao.setDefaultValue(strDefault);
		metaDataDao.setPrimaryKey(isPrimaryKey);
		metaDataDao.setNotNull(isNotNull);
		metaDataDao.setAutoIncrement(isAutoIncrement);
		metaDataDao.setCollation(strCollation);
		metaDataDao.setComment(strComment);
		
		try {
			TableColumnObjectQuery.addColumn(userDB, tableDAO, metaDataDao);
			refreshTableColumn();
			MessageDialog.openInformation(null, "확인", "컬럼이 추가되었습니다.");
			textColumnName.setFocus();
		} catch (Exception e) {
			logger.error("add column exception", e);
			
			TDBErroDialog errDialog = new TDBErroDialog(null, Messages.get().ObjectDeleteAction_25, "Column name을 추가하는 중에 오류가발생했습니다.\n" + e.getMessage());
			errDialog.open();
		}
	}
	

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "CANCEL", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 405);
	}
	
	/**
	 * refresh table column
	 * @return
	 */
	private void refreshTableColumn() {
		try {
			ExplorerViewer ev = (ExplorerViewer)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ExplorerViewer.ID);
			if(ev != null) ev.refreshTableColumn();
		} catch(Exception e) {
		}
	}
}
