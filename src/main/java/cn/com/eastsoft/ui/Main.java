package cn.com.eastsoft.ui;

public class Main {
	public static void main(String[] args){
		String lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		String lookAndFeel_now= javax.swing.UIManager.getSystemLookAndFeelClassName();
		String windows_format="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try
		{
			javax.swing.UIManager
					.setLookAndFeel(lookAndFeel_now);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		MainJFrame inst = MainJFrame.getInstance();
		/*for(int i=0;i<26;i++){
			MainJFrame.showMssage("asdfasfdas\n");
			try {
	            Thread.sleep(200);
	        } catch (InterruptedException e) {
	            e.printStackTrace(); 
	        }
		}*/
	}
}
