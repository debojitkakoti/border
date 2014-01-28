/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package border;
import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author debojit
 */
public class mainframe extends javax.swing.JFrame {

   



    /**
     * Creates new form mainframe
     */
    public mainframe() {
        initComponents();

    }
    
    Vector<String> candidates=new Vector<String>(); //the current candidate
    Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
    static Vector<String> PfrequentCandidates = new Vector<String>();
    Vector<String> notfrequent=new Vector<String>();//To store Non Frequent Itemst to find Border set
    static Vector<String> BorderSet=new Vector<String>();
    Vector<String> SubsetGen=new Vector<String>();
    static Vector<Double> fsup=new Vector<Double>();//These are count 
    static Vector<Double> bsup=new Vector<Double>();//These are count not support
    String delim=" ";

    int numTransactions=5; //number of transactions.....Later I will Change it 
   static double minSup; //minimum support for a frequent itemset
   static double SecminSup;//minimum support for beta
   static String table;
   String dataurl="jdbc:mysql://localhost:3306/deboj11";
   String uname="root";
   String pass="";
  
    /************************************************************************
     * Method Name  : aprioriProcess
     * Purpose      : Generate the apriori itemsets
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    private void aprioriProcess()
    {
     candidates.clear();
     frequentCandidates.clear();
     PfrequentCandidates.clear();
     notfrequent.clear();
     BorderSet.clear();
     SubsetGen.clear();
     fsup.clear();
     bsup.clear();
 
   
     frequentCandidates.clear();
     int itemsetNumber=0; //the current itemset being looked at
   
        //while not complete
        do
        {
            //increase the itemset that is being looked at
            itemsetNumber++;

            //generate the candidates
            generateCandidates(itemsetNumber);

            //determine and display frequent itemsets
            calculateFrequentItemsets(itemsetNumber);
        }while(candidates.size()>1);
        CalculateBorderSet();
    }

    /************************************************************************
     * Method Name  : generateCandidates
     * Purpose      : Generate all possible candidates for the n-th item sets
     *              : these candidates are stored in the candidates class vector
     * Parameters   : n - integer value representing the current item sets to be created
     * Return       : None
     *************************************************************************/
    private void generateCandidates(int n)
    {
        Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
        String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared

        //if its the first set, candidates are just the numbers
        if(n==1)
        {
          tempCandidates.add("A");
          tempCandidates.add("B");
          tempCandidates.add("C");
          tempCandidates.add("D");
          tempCandidates.add("E");  
        }
        else if(n==2) //second itemset is just all combinations of itemset 1
        {
            //add each itemset from the previous frequent itemsets together
            for(int i=0; i<candidates.size(); i++)
            {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for(int j=i+1; j<candidates.size(); j++)
                {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1+" "+str2);
                }
            }
        }
        else
        {
            //for each itemset
            for(int i=0; i<candidates.size(); i++)
            {
                //compare to the next itemset
                for(int j=i+1; j<candidates.size(); j++)
                {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i),delim);
                    st2 = new StringTokenizer(candidates.get(j),delim);

                    //make a string of the first n-2 tokens of the strings
                    for(int s=0; s<n-2; s++)
                    {
                        str1 = str1 + " " + st1.nextToken();
                        str2 = str2 + " " + st2.nextToken();
                    }

                    //if they have the same n-2 tokens, add them together
                    if(str2.compareToIgnoreCase(str1)==0){
                        tempCandidates.add((str1 + " "+ st1.nextToken() + " " + st2.nextToken()).trim());
                    }
                }
            }
        }
        //clear the old candidates
        candidates.clear();
        //set the new ones
        candidates = new Vector<String>(tempCandidates);
        tempCandidates.clear();
    }

    
    /************************************************************************
     * Method Name  : calculateFrequentItemsets
     * Purpose      : Determine which candidates are frequent in the n-th item sets
     *              : from all possible candidates
     * Parameters   : n - integer representing the current item sets being evaluated
     * Return       : None
     *************************************************************************/
    
    private void calculateFrequentItemsets(int n)
    {
      

        StringTokenizer st; //tokenizer for candidate and transaction
        boolean match; //whether the transaction has all the items in an itemset
        double count[] = new double[candidates.size()]; //the number of successful matches
        Connection conn=null;
        Statement stmt=null;  
        double sup;
        
        

           try{
            Class.forName("com.mysql.jdbc.Driver");//Register Driver
            conn = DriverManager.getConnection(dataurl,uname,pass);//Open Database Connection
            stmt = conn.createStatement();
            String sql;
            
            sql = "SELECT * FROM "+table;
            ResultSet rs = stmt.executeQuery(sql);
            //For Each Transaction
            while(rs.next()){
            //Check Each Candidate
                for(int c=0;c<candidates.size();c++){
                    match = false;//reset match to false
                    st = new StringTokenizer(candidates.get(c),delim);//Tokenize each item in itemset
                    //check each item in the itemset in the transaction if it is present 
                    while(st.hasMoreTokens()){
                    int val=rs.getInt(st.nextToken());
                        if(val==1){
                            match=true;
                        }
                        else{match=false;}
                        if(!match){break;}
                    }
                    if(match){count[c]++;}//if successful match then increment the count
                }//For loop Close 
            }//While Loop Close For each transaction
            rs.close();
            }
         catch(SQLException se){
            se.printStackTrace();//Handles Error For Jdbc
            }
         catch(Exception e){
            e.printStackTrace();//Handles Error For class.name
            }
  
            
                //Calculate Support to compare with Minimum Support   
                for(int i=0; i<candidates.size(); i++)
                {
                    //calculate support 
                    //sup=count[i]/(double)numTransactions;
                    //if the count% is larger than the minSup%, add to the candidate to the frequent candidates
                    if(count[i]/(double)numTransactions >= minSup)
                    {
                        frequentCandidates.add(candidates.get(i));
                        //Permanently Store to display later
                        PfrequentCandidates.add(candidates.get(i));
                        //store support
                       fsup.add(count[i]);

                    }
                    else{
                        //If not Frequent Then Store Here to Find Border Set Later
                        notfrequent.add(candidates.get(i));
                        bsup.add(count[i]);
                        
                    }
                }
               
   
        //clear old candidates
        candidates.clear();
        //new candidates are the old frequent candidates
        candidates = new Vector<String>(frequentCandidates);
        frequentCandidates.clear();
    
}
    
/***********************************************
Method Name:SubsetGeneration
*Purpose:Generate Subset
*Return Type:None
*Parameters:Non Frequent item String
*Other Method Used:None 
***********************************************/        
   private void SubsetGeneration(String s){
   Vector<String> tmpsubset1=new Vector<String>();
   Vector<String> tmpsubset2=new Vector<String>();
   String substr1,substr2,str;
   int len;
   StringTokenizer subtoken,subtokenst1,subtokenst2;
   len=s.length()-1;
   /* if String length is==2 it will run 1 pass and add chars in vector if length is 3 it will run 2 times */
for(int i=1;i<len;i++){
     if(i==1){
     subtoken=new StringTokenizer(s,delim);
     while(subtoken.hasMoreTokens()){
         str=subtoken.nextToken();
         SubsetGen.add(str);
         tmpsubset1.add(str);
        }//While Loop Close
     tmpsubset2.clear();
     tmpsubset2=new Vector(tmpsubset1);
     }//if length is 1
     else if(i==2){
         tmpsubset1.clear();
         //add each itemset from the previous frequent itemsets together
            for(int k=0; k<tmpsubset2.size();k++)
            {
                subtokenst1 = new StringTokenizer(tmpsubset2.get(k));
                substr1 = subtokenst1.nextToken();
                for(int l=k+1; l<tmpsubset2.size();l++)          {
                    subtokenst2 = new StringTokenizer(tmpsubset2.get(l));
                    substr2 = subtokenst2.nextToken();
                    tmpsubset1.add(substr1+substr2);
                    SubsetGen.add(substr1+" "+substr2);
                }
            }
         tmpsubset2.clear();
         tmpsubset2=new Vector(tmpsubset1);
     }//IF length==2 close
     else{
         tmpsubset1.clear();
                //for each itemset
            for(int m=0; m<tmpsubset2.size();m++)
            {
                //compare to the next itemset
                for(int n=m+1; n<tmpsubset2.size();n++)
                {
                    //create the strigns
                    substr1 = new String();
                    substr2 = new String();
                    //create the tokenizers
                    subtokenst1 = new StringTokenizer(tmpsubset2.get(m));
                    subtokenst2 = new StringTokenizer(tmpsubset2.get(n));

                    //make a string of the first n-2 tokens of the strings
                    for(int p=0; p<len-2; p++)
                    {
                        substr1 = substr1 + " " + subtokenst1.nextToken();
                        substr2 = substr2 + " " + subtokenst2.nextToken();
                    }

                    //if they have the same n-2 tokens, add them together
                    if(substr2.compareToIgnoreCase(substr1)==0){
                        String tmpstring;
                        tmpstring=(substr1 + subtokenst1.nextToken() + subtokenst2.nextToken()).trim();
                       tmpsubset1.add(tmpstring);
                       SubsetGen.add(tmpstring);
                    }
                }
            }
            tmpsubset2.clear();
            tmpsubset2=new Vector(tmpsubset1);
            
     }//else close
}//For Loop Close
     
}
    
/***********************************************
Method Name:CalculateBorderSet
*Purpose:Generate Border Set
*Return Type:None
*Parameters:Itemset Number
*Other Method Used:SubsetGeneration() 
***********************************************/    
    
private void CalculateBorderSet(){
boolean matchin=true;
for(int j=0;j<notfrequent.size();j++){
   //IF LENGTH IS OF 1 Then no subset so add directly to Border Set.
   if(notfrequent.get(j).length()==1){
   BorderSet.add(notfrequent.get(j));
   }
   else{
   SubsetGeneration(notfrequent.get(j));
   for(int w=0;w<SubsetGen.size();w++){
   for(int e=0;e<PfrequentCandidates.size();e++){
   matchin=SubsetGen.get(w).equals(PfrequentCandidates.get(e));
   if(matchin){break;}//break from inner loop
   }
   if(!matchin){bsup.remove(j);break;}//break from second inner loop no comparison required
   }//For loop for subset comparing
   if(matchin){
   BorderSet.add(notfrequent.get(j));
   }
   SubsetGen.clear();
   }
}//For Loop Close
notfrequent.clear();
}
     



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ARM FOR SENSOR NETWORK");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(102, 102, 102));

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setText("Select Data");

        jLabel2.setText("First Min. Support");

        jLabel3.setText("Second Min. Support");

        jButton1.setText("Run");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(53, 53, 53))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(25, 25, 25))
        );

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        jLabel4.setText("Frequent Set For Old data");

        jLabel5.setText("Border Set For Old Data");

        jComboBox2.setAutoscrolls(true);
        jComboBox2.setDoubleBuffered(true);

        jComboBox3.setAutoscrolls(true);
        jComboBox3.setDoubleBuffered(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));

        jButton2.setText("Border Algorithm");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton3.setText("Modified Border");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(204, 204, 204)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(195, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );

        jMenuBar1.setBackground(new java.awt.Color(102, 102, 102));

        jMenu1.setText("Connect");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Exit");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
// TODO add your handling code here:
    jComboBox2.removeAllItems();
    jComboBox3.removeAllItems();

    minSup=Double.parseDouble(jTextField1.getText());
    SecminSup=Double.parseDouble(jTextField2.getText());
    table=jComboBox1.getSelectedItem().toString();

    aprioriProcess();


    for(int v=0;v<PfrequentCandidates.size();v++){
    jComboBox2.addItem(PfrequentCandidates.get(v));
    }
        for(int v=0;v<BorderSet.size();v++){
        jComboBox3.addItem(BorderSet.get(v));
    }
   // jComboBox3.addItem(ap.BorderSet);

}//GEN-LAST:event_jButton1MouseClicked


private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
// TODO add your handling code here:
        Connection conn=null;
        jComboBox1.removeAllItems();

           try{
           Class.forName("com.mysql.jdbc.Driver");//Register Driver
           conn = DriverManager.getConnection(dataurl,uname,pass);//Open Database Connection
           DatabaseMetaData dbm=conn.getMetaData();
           
           /* DISPLAYING LIST OF TABLES */
           String[] types={"TABLE"};
           ResultSet rs=dbm.getTables(null, null, "%", types);
           while(rs.next()){
           String table=rs.getString("TABLE_NAME");
           jComboBox1.addItem(table);
           }
           rs.close();
           conn.close();
         
           }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(mainframe.class.getName()).log(Level.SEVERE, null, ex);
            }          
        catch(SQLException se){
           se.printStackTrace();//Handles Error For Jdbc
           }
}//GEN-LAST:event_jMenu1MouseClicked

private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
// TODO add your handling code here:
    Bordergui b=new Bordergui();
    b.setAlwaysOnTop(true);
    b.setVisible(true);
   
    
    
}//GEN-LAST:event_jButton2MouseClicked

private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
// TODO add your handling code here:
    ModifiedBorderGui mb=new ModifiedBorderGui();
    mb.setAlwaysOnTop(true);
    mb.setVisible(true);
    
}//GEN-LAST:event_jButton3MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
             for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

      
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainframe().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

}
