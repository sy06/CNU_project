package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUIView extends JFrame {

	public static GUIView view;
	
	 //�޴��ٸ� ���� ���� 
	private ActionListener _exitListener;
	private ActionListener _stopListener;
	private ActionListener _startListener;
	private ActionListener _restartListener;
	private JMenu menu;
	private JMenuBar menuBar;
	private JMenuItem exitItem;
	private JMenuItem stopItem;
	private JMenuItem startItem;
	private JMenuItem restartItem;
	
	private JPanel panel;
	private JTextArea logTxt;
	int linecount;
	StringBuffer msg;

	//gui init
	public GUIView(ActionListener exitL, ActionListener stopL, ActionListener startL, ActionListener restartL) {
		this._exitListener = exitL;
		this._stopListener = stopL;
		this._startListener = startL;
		this._restartListener = restartL;
		setLayout(null);
		createMenu();
		createPanel();
		setTitle("Server_UI");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 1000);
		setVisible(true);
		this.linecount = 0;
		this.msg = new StringBuffer();
	}

	//private methods
	//which makes Layout
	private void createMenu() {
		menuBar = new JMenuBar(); // �޴��ٸ� ����
		menu = new JMenu("����"); // '����'�̶�� �޴��� ����
		menuBar.add(menu); // �޴��ٿ� '����'�̶�� �޴��� �߰�
		//STARTPOINT : Menu ����
		exitItem = new JMenuItem("���α׷� ����"); // '��������'��� �޴������� ����
		menu.add(exitItem); // '����' �޴� �ȿ� '��������'��� �޴������� �߰�
		exitItem.addActionListener(_exitListener); // '��������' �޴� ������ ���ý� �߻��ϴ� �̺�Ʈ ����
		
		stopItem = new JMenuItem("���� ����"); // '��������'��� �޴������� ����
		menu.add(stopItem); // '����' �޴� �ȿ� '��������'��� �޴������� �߰�
		stopItem.addActionListener(_stopListener); // '��������' �޴� ������ ���ý� �߻��ϴ� �̺�Ʈ ����
		
		startItem = new JMenuItem("���� ����"); // '��������'��� �޴������� ����
		menu.add(startItem); // '����' �޴� �ȿ� '��������'��� �޴������� �߰�
		startItem.addActionListener(_startListener); // '��������' �޴� ������ ���ý� �߻��ϴ� �̺�Ʈ ����
		
		restartItem = new JMenuItem("���� �����"); // '��������'��� �޴������� ����
		menu.add(restartItem); // '����' �޴� �ȿ� '��������'��� �޴������� �߰�
		restartItem.addActionListener(_restartListener); // '��������' �޴� ������ ���ý� �߻��ϴ� �̺�Ʈ ����
		//ENDPOINT : Menu ����
		
		menuBar.setBorder(BorderFactory.createLineBorder(Color.gray)); // �޴��� ���� ����


		setJMenuBar(menuBar);
	}
	
	private void createPanel() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBounds(0, 0, 600, 800);
		
		logTxt = new JTextArea();
		logTxt.setBounds(50,50,200,300);
		logTxt.setEditable(false);
		addMessage("Welcome to..\n2017 CNU_CSE OOP Termproject <<Palette>> Server!!");

		JScrollPane scroll = new JScrollPane(logTxt);
		
		panel.add(scroll);
		add(panel);
	}
	
	//public method
	public void addMessage(String addTxt) {
		this.linecount++;
		
		this.logTxt.setText(addTxt+"\n"+this.logTxt.getText());//���� ���� �ʿ�. �޸� ����, �߰���ġ ����
	}
	public void setExitListener(ActionListener _exitListener) {
		this._exitListener = _exitListener;
		exitItem.addActionListener(_exitListener);
	}
	public void setStopListener(ActionListener _stopListener) {
		this._stopListener = _stopListener;
		stopItem.addActionListener(_stopListener);
	}
	public void setStartListener(ActionListener _startListener) {
		this._startListener = _startListener;
		startItem.addActionListener(_startListener);
	}
	public void setRestartListener(ActionListener _restartListener) {
		this._restartListener = _restartListener;
		restartItem.addActionListener(_restartListener);
	}
	private class MSGQueue{
		private String[] content;
		private int length;
		private int size;
		private int front;
		private int back;
		public MSGQueue(int length) {
			this.content = new String[length];
			this.front = 0;
			this.back = length-1;
			this.size = 0;
			this.length = length;
		}
		public void add(String s) {
			this.content[front] = s;
			front = (front+1)%length;
			if(back==front) {
				back = (back+1)%length;
			}
		}
	}
}
