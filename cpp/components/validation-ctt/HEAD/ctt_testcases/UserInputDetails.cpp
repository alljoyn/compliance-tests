/******************************************************************************
* Copyright AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for any
*    purpose with or without fee is hereby granted, provided that the above
*    copyright notice and this permission notice appear in all copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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