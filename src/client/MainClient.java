package client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import common.Settings;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.viewers.ListViewer;

public class MainClient extends Shell {
	private Text text;
	private Client client;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			MainClient shell = new MainClient(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public MainClient(Display display) throws UnknownHostException, IOException {
		super(display, SWT.SHELL_TRIM);
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					client.close();
				} catch (Exception exception) {
				}
			}
		});
		setLayout(new GridLayout(1, false));
		new Label(this, SWT.NONE);
		
		ListViewer listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		List list = listViewer.getList();
		GridData gd_list = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_list.heightHint = 516;
		gd_list.widthHint = 800;
		list.setLayoutData(gd_list);
		
		text = new Text(this,SWT.BORDER | SWT.WRAP);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (e.keyCode == SWT.CR && text.getText().trim().length() > 0) {
						client.send(text.getText().trim());
						text.setText("");
					}
				} catch (Exception exception) {
				}
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		createContents();
		client = new Client(Settings.HOST, Settings.PORT, message -> {
			Display.getDefault().asyncExec(() -> {
				list.add(message);
				list.redraw();
			});
		});
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Computer Newtorking Project");
		setSize(868, 668);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}