package picDataConsole;
 
import picDataConsole.databean;
import java.awt.Container;  

import java.awt.GridLayout;  
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;  
import javax.swing.JScrollPane;  
import javax.swing.JTextArea;  

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcReader;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class picDataConsole {
	private static void print(Metadata metadata)
    {
        System.out.println("-------------------------------------");

        // Iterate over the data and print to System.out

        //
        // A Metadata object contains multiple Directory objects
        //
        for (Directory directory : metadata.getDirectories()) {

            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }

            //
            // Each Directory may also contain error messages
            //
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.println("ERROR: " + error);
                }
            }
        }
    }
    
            
	public static void main(String[] args){
		    final JFileChooser fileChooser = new JFileChooser(".");
    		JFrame frame=new JFrame("PicDataConsole");
    		final JTextArea picDir=new JTextArea();
    		JButton button=new JButton("选择文件夹");
    		 MouseListener ml = new MouseListener() {  
    	         public void mouseClicked(MouseEvent e) {  
    	             if (e.getClickCount() == 2) {  
    	            	 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	            	 fileChooser.setDialogTitle("打开文件夹");
    	            	 int ret = fileChooser.showOpenDialog(null);
    	            	 if (ret == JFileChooser.APPROVE_OPTION) {
    	            		//文件夹路径
    	            		 String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
    	            		System.out.println(absolutepath);
    	            		picDir.setText(absolutepath);
    	            		}
    	                String dir= picDir.getText();
    	                File d=new File(dir);
    	        		File list[] = d.listFiles();
                        databean insert=new databean();
                        Connection conn = null;
                        ResultSet rs=null;
                        insert.setDB("pic data");
                        conn=insert.getConn();
                        String model=null;
                        String time=null;
                        String dpi=null;
                        String location=null;
						//search for the biggest id in the pic database;
//                        String searchforbig="select maxd(id) from pic database";
//                        rs=insert.executeSQL(searchforbig);
                        
    	        		for(int i = 0; i < list.length; i++){
    	        			 String path=list[i].getAbsolutePath();
                            try {
								Metadata metadata = ImageMetadataReader.readMetadata(list[i]);
								for (Directory directory : metadata.getDirectories()) {
						            for (Tag tag : directory.getTags()) {
						                String tagName = tag.getTagName();
						                String desc = tag.getDescription();
						                if (tagName.equals("X Resolution")) {
						                    //图片dpi
						                    dpi=desc;
						                } else if (tagName.equals("Date/Time Original")) {
						                    //拍摄时间
						                    time=desc;
						                } else if (tagName.equals("Model")) {
						                    //手机型号
						                    model=desc;
						                }
						            }
								}
								String l[]=list[i].getParent().split("\\\\");
								int lg=l.length;
								location=l[lg-1];

								
								//insert into database picdata;
								//search for latest time pic;
								String sqllocation="select MAX(id) from picdata where location = '"+location+"'";
								ResultSet latestlocation=insert.executeSQL(sqllocation);
								int llocation=0;
								try {
									latestlocation.next();
									llocation=latestlocation.getInt(1);
								} catch (SQLException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}
								
								if(llocation>0){
								String sqltime="select MAX(time) from picdata where location = '"+location+"'";
								ResultSet latesttime=insert.executeSQL(sqltime);
								
								int t = 0;
								try {
									
									latesttime.next();
									String ltime=latesttime.getString(1);
									if(ltime!=null)
									t = time.compareTo(ltime);
									
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								if(t>0){
								String sql="insert into picdata values(null,'"+path+"','"+time+"','"+model+"','"+location+"','"+dpi+"')";
						        insert.executeUpdateSQL(sql);
								}
								}
								else{
									String sql="insert into picdata values(null,'"+path+"','"+time+"','"+model+"','"+location+"','"+dpi+"')";
							        insert.executeUpdateSQL(sql);
								}
								System.out.println(time);
								System.out.println(dpi);
								System.out.println(model);
								System.out.println(location);	
								System.out.println(path);	
							} catch (ImageProcessingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                            
    	        		}
    	                
    	                try {
							conn.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
    	                 
    	                 
    	                 
    	                 
    	                 
    	                 
    	                               }  
    	                     }  
    	                   public void mouseEntered(MouseEvent e) {  
    	             // TODO Auto-generated method stub  

    	         }  

    	         public void mouseExited(MouseEvent e) {  
    	             // TODO Auto-generated method stub  

    	         }  

    	         public void mousePressed(MouseEvent e) {  
    	             // TODO Auto-generated method stub  

    	         }  

    	         public void mouseReleased(MouseEvent e) {  
    	             // TODO Auto-generated method stub  

    	         }  
    	            }; 
    	   button.addMouseListener(ml);
    	   button.setSize(20,10);
    	   picDir.setSize(10,20);

    	   Container content = frame.getContentPane();  
    	   content.setLayout(new GridLayout(1,2));  
    	   content.add(picDir);
    	   content.add(button);
    	   frame.setSize(400, 300); 
    	   frame.setVisible(true);
    	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
       }

	
	

}
