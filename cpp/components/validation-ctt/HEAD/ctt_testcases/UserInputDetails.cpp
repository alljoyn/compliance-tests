/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

******************************************************************************/
#include "stdafx.h"
#include "UserInputDetails.h"

#include "NotificationDialogResources.h"

UINT CALLBACK ReturnSelectedOptionProc(HWND hwndDlg,
	UINT message,
	WPARAM wParam,
	LPARAM lParam)
{
	switch (message)
	{
	case WM_COMMAND:
	{
		switch (LOWORD(wParam))
		{
		case IDC_BUTTON1:
		{
			EndDialog(hwndDlg, 1);
			break;
		}
		case IDC_BUTTON2:
		{
			EndDialog(hwndDlg, 2);
			break;
		}
		case IDC_BUTTON3:
		{
			EndDialog(hwndDlg, 3);
			break;
		}
		case IDC_BUTTON4:
		{
			EndDialog(hwndDlg, 4);
			break;
		}
		case IDC_BUTTON5:
		{
			EndDialog(hwndDlg, 5);
			break;
		}
		case IDC_BUTTON6:
		{
			EndDialog(hwndDlg, 6);
			break;
		}
		}
	}
	default:
		return 0;
	}
}

UserInputDetails::UserInputDetails(HINSTANCE t_HInstance, int t_SelectedDialog, HWND t_HWndParent)
{
	int lpTemplate = 0;

	switch (t_SelectedDialog)
	{
	case 1:
	{
			  lpTemplate = NOTIF_V1_01_DIALOG;
			  break;
	}
	case 2:
	{
			  lpTemplate = NOTIF_V1_02_DIALOG;
			  break;
	}
	case 4:
	{
			  lpTemplate = NOTIF_V1_04_DIALOG;
			  break;
	}
	case 5:
	{
			  lpTemplate = NOTIF_V1_05_DIALOG;
			  break;
	}
	case 6:
	{
			  lpTemplate = NOTIF_V1_06_DIALOG;
			  break;
	}
	default:
	{
			  lpTemplate = 0;
	}
	}

	m_SelectedOption = DialogBox(
		t_HInstance,
		MAKEINTRESOURCE(lpTemplate),
		t_HWndParent,
		(DLGPROC)ReturnSelectedOptionProc
		) - 1;
}

int UserInputDetails::getOptionSelected()
{
	return m_SelectedOption;
}