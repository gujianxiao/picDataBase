package picDataConsole;
 
import picDataConsole.databean;
import java.awt.Container;  
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.TextField;

import java.awt.GridLayout;  
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;  
import javax.swing.JPanel;
import javax.swing.JScrollPane;  
import javax.swing.JTextArea;  

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    		JButton pm=new JButton("导入 pm2.5 数据");
     	    final Container content = frame.getContentPane(); 
     	    MouseListener pm25=new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
                    databean insert=new databean();
                    Connection conn = null;
                    ResultSet rs=null;
                    insert.setDB("pic data");//set database name
                    conn=insert.getConn();//connect to database
                    String sql=null;
					 if (e.getClickCount() == 2) { 
					 fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					 fileChooser.setDialogTitle("打开pm2.5数据文件");
	            	 int ret = fileChooser.showOpenDialog(null);
	            	 if (ret == JFileChooser.APPROVE_OPTION) {
	            		//文件选取
	            		File pmdata=fileChooser.getSelectedFile();
                      
							BufferedReader pmReader;
							try {
								pmReader = new BufferedReader(new FileReader(pmdata));
								String temp=null;
								String[] tp=null;
								ArrayList<String[]> pmValue=new ArrayList<String[]>();
								while((temp=pmReader.readLine())!= null){
									tp=temp.split(",");
									pmValue.add(tp);
								}
								// select the range of time to detect error
                            	String searchSql="select Min(time) from picdata where location='北邮'";                         	
                            	rs=insert.executeSQL(searchSql);
                            	rs.next();
                            	String minTime=rs.getString(1);
                            	searchSql="select Max(time) from picdata where location='北邮'";
                            	rs=insert.executeSQL(searchSql);
                            	rs.next();
                            	String maxTime=rs.getString(1);
                                //   iterator the pmValue to update database
                                for(int i=0;i<pmValue.size();i++){
                                	String[] dl1=pmValue.get(i);
                               	String[] dl2=pmValue.get(i+1);                              	
                                	if(dl1[0].equalsIgnoreCase("001008")){
                                		String beginTime=dl1[1].replace('-', ':');
                                		String endTime=dl2[1].replace('-', ':');
                                		if((beginTime.compareTo(minTime)>0)&&(endTime.compareTo(maxTime)<0)){
                                        String updateSql="update picdata set pm25='"+dl1[2]+"',pm10='"+dl1[3]+"',co2='"+dl1[4]+"' where '"+beginTime+"'<time and time<'"+endTime+"'";
        								insert.executeUpdateSQL(updateSql); 		
                                		}
                                		else{
                                			System.out.println("out of the time range!");
                                		}
                                	}
                                }
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			
	            		}
						
						 
						 
					 }
					 
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
     	    	
     	    };
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
    	        		File list[] = d.listFiles();//file list
                        databean insert=new databean();
                        Connection conn = null;
                        ResultSet rs=null;
                        insert.setDB("pic data");//set database name
                        conn=insert.getConn();//connect to database
                        String model=null;
                        String time=null;
                        String dpi=null;
                        String location=null;
                        int piccount=list.length;
                        int addcount=0;

 // iterate every picture                       
    	        		for(int i = 0; i < list.length; i++){
    	        			 
    	        			 String path=list[i].getAbsolutePath();
    	        			 path=path.replaceAll("\\\\", "\\\\\\\\");//replace the "\\" to "\\\\" so that can be insert into database in "\"
    	        			 String name=list[i].getName();
                            try {
								Metadata metadata = ImageMetadataReader.readMetadata(list[i]);//create a Metadata class to read the info of picture
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

								
							
								//search for same time pic;
								String lastdata="select * from picdata where model = '"+model+"' and time='"+time+"'";
								ResultSet latest=insert.executeSQL(lastdata);
									if(!latest.next()){// while there is no same picture,then insert it into database
									addcount++;
								String sql="insert into picdata values(null,'"+path+"','"+name+"','"+model+"','"+location+"','"+dpi+"','"+time+"',null,null,null)";
						        int r=insert.executeUpdateSQL(sql);
						        //print the pic info for debug
								System.out.println(time);
								System.out.println(dpi);
								System.out.println(model);
								System.out.println(location);	
								System.out.println(path);
								System.out.println(name);
									}
							} catch (ImageProcessingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                            
    	        		}
    	                //show the console info;
    	        		TextField consoleInfo=new TextField();
    	                consoleInfo.setText(location+"处理完毕！图片集共有图片"+piccount+"张，新增图片"+addcount+"张。");
    	                content.add(consoleInfo);
    	                 
    	                 
    	                 
    	                 
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
    	   pm.addMouseListener(pm25);
    	   JPanel txt=new JPanel();
    	   picDir.setColumns(15);
    	   picDir.setRows(1);
           txt.add(picDir); 
    	   content.setLayout(new FlowLayout(FlowLayout.LEFT));  
    	   content.add(txt);
    	   content.add(button);
    	   content.add(pm);
    	   frame.setSize(500,600);
    	   frame.setVisible(true);
    	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
       }

	
	

}
