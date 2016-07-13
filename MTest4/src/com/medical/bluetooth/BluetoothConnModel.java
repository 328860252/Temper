/*
 * Copyright (C) 2011 Wireless Network and Multimedia Laboratory, NCU, Taiwan
 * 
 * You can reference http://wmlab.csie.ncu.edu.tw
 * 
 * This class is used to process connection operation, including server side or client side. * 
 * 
 * @author Fiona
 * @version 0.0.1
 *
 */

package com.medical.bluetooth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import com.example.medical.MainActivity2;
import com.medical.help.Myhex;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class BluetoothConnModel {
	// Debugging
	private static final boolean D = true;   
    private static final String TAG = "BluetoothConnModel";     
    private static final String NAME = "BluetoothConn";
    private static final UUID CUSTOM_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID CUSTOM_UUID = UUID.fromString("a2221b09-ed5c-464b-9a1f-96f188ad97e5");
    public static final String MONITOR_OUTPUT_NAME = "output.txt";
    
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;  
    private final Context mContext;
   // private Map<BluetoothDevice, BluetoothSocketConfig> mBluetoothSocekts;
    private ServerSocketThread mServerSocketThread;
    private BluetoothSocketConfig mSocketConfig = null;
    private FileOutputStream mOutputFile;
    private boolean mMonitor = false;
    private int mTxBytes = 0;
    private int mRxBytes = 0;
    private int mMonitorBytes = 0;
    
    
    private boolean isLink = false;
    
    public int getTxBytes() {
    	return mTxBytes;
    }
    public int getRxBytes() {
    	return mRxBytes;
    }
    public boolean getFileMonitor() {
    	return mMonitor;
    }
    public void startFileMonitor(boolean b) {
    	Log.d(TAG, "startFileMonitor " + b);
    	mMonitor = b;
    	if (mMonitor == true){
           	File root = Environment.getExternalStorageDirectory();
            try {
            	mOutputFile = new FileOutputStream(root + "/" + MONITOR_OUTPUT_NAME, false);
            }
            catch (Exception e) {
            	Log.e(TAG, "new FileOutputStream fail", e);
            }	
    	}
    	else {
    		try {
    			mOutputFile.close(); 			
    		}
    		catch(Exception e){
    			
    		}
    	}
    }
    public BluetoothConnModel(Context context, Handler handler) {
    	mAdapter = BluetoothAdapter.getDefaultAdapter();
    	mHandler = handler;
    	mContext = context;
  //  	mBluetoothSocekts = new HashMap<BluetoothDevice, BluetoothSocketConfig> ();
    	
    }
    
    public synchronized void startSession(){
    	if (D) Log.d(TAG, "[startSession] ServerSocketThread start...");
    		
    	if (mServerSocketThread == null) {
    		Log.i(TAG, "[startSession] mServerSocketThread is dead");
    	    mServerSocketThread = new ServerSocketThread();
    	    mServerSocketThread.start();
    	} else {
    		Log.i(TAG, "[startSession] mServerSocketThread is alive : "+this);
    	}
    	
    	mSocketConfig = BluetoothSocketConfig.getInstance();
    }
    public synchronized void connectTo(BluetoothDevice device){
    	if (D) Log.d(TAG, "[connectTo] ClientSocketThread start...");
    	SocketThread mSocketThread = new SocketThread(device);
     	mSocketThread.start();
    }
    
    public synchronized void connected(BluetoothSocket socket){
    	if (D) Log.d(TAG, "[connected]");
    	//BluetoothSocketConfig socketConfig = new BluetoothSocketConfig();
    	//notifyUiFromToast(socket.getRemoteDevice().getName()+" has connected.");
    	mHandler.sendEmptyMessage(MainActivity2.handler_LinkSuccess);
    	isLink = true;
    	mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1, socket.getRemoteDevice().getName()+" has connected.").sendToTarget();
  		ConnectedThread connectedThread = new ConnectedThread(socket);
  		if (mSocketConfig.registerSocket(socket, connectedThread, BluetoothSocketConfig.SOCKET_CONNECTED) == false) {
        	
        	mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1, "Device link back again!").sendToTarget();
  		}
  		Log.e(TAG, "[connected] connectedThread hashcode = "+connectedThread.toString());
	//	socketConfig.setBluetoothSocket(socket);
	////	socketConfig.setSocketState(BluetoothSocketConfig.SOCKET_CONNECTED);
	//	socketConfig.setConnectedThread(connectedThread);
	//	mBluetoothSocekts.put(socket.getRemoteDevice(), socketConfig);
		connectedThread.start();
    }
    
    public boolean isLink(){
    	return isLink;
    }

    /*
    public void write(byte[] out) {
    	Set<BluetoothDevice> devices = null;
    	for (BluetoothDevice device : mBluetoothSocekts.keySet()) {
    		BluetoothSocket btSocket= socketConfig.getBluetoothSocket();
    		if (!devices.contains(device))
    			devices.add(device);
    	}    	
    	writeToSockets(sockets, out);
    }
    */
    public void SendFileToSocket(BluetoothSocket socket, String file){
    	SendFileThread sendFile = new SendFileThread(socket, file); 
    	sendFile.start();   	
    }
    public void SendFileToAllSockets(String file) {
    	if (D) Log.d(TAG, "SendFileAllSockets start...");
    	for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
    	    synchronized (this) {
                //  if (mState != STATE_CONNECTED) return;
    	    	SendFileToSocket(socket, file.toString()); 
            }
    	}
    }
    public void writeToSocket(BluetoothSocket socket, String out) {
    	if (D) Log.d(TAG, "writeToDevice start...");
    	ConnectedThread connectedThread = mSocketConfig.getConnectedThread(socket);    	 
    	Log.e(TAG, "[writeToDevice] connectedThread hashcode = "+connectedThread.toString());
    	 if (mSocketConfig.isSocketConnected(socket)){
    		Log.w(TAG, "[writeToDevice] The socket is alived.");
 	    	connectedThread.write(out);
    	 }else
    		Log.w(TAG, "[writeToDevice] The socket has been closed.");
    }
    
    public void writeToSocket(BluetoothSocket socket,byte[] buffer) {
    	if (D) Log.d(TAG, "writeToDevice start...");
    	ConnectedThread connectedThread = mSocketConfig.getConnectedThread(socket);    	 
    	Log.e(TAG, "[writeToDevice] connectedThread hashcode = "+connectedThread.toString());
    	 if (mSocketConfig.isSocketConnected(socket)){
    		Log.w(TAG, "[writeToDevice] The socket is alived.");
 	    	connectedThread.write(buffer);
    	 }else
    		Log.w(TAG, "[writeToDevice] The socket has been closed.");
    }
    
    public void writeToSockets(Set<BluetoothSocket> sockets, String out) {
    	if (D) Log.d(TAG, "writeToDevices start...");
    	for (BluetoothSocket socket : sockets) {
    	    synchronized (this) {
                //  if (mState != STATE_CONNECTED) return;
    	    	writeToSocket(socket, out); 
            }
    	}
    }
    public void writeToAllSockets(String out) {
    	if (D) Log.d(TAG, "writeToAllDevices start...");
    	for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
    	    synchronized (this) {
                //  if (mState != STATE_CONNECTED) return;
    	    	writeToSocket(socket, out); 
    	    	Log.e(TAG, "[writeToAllDevices] currentTimeMillis: "+ System.currentTimeMillis() );
            }
    	}
    }
    public void writeToAllSockets(byte[] out) {
    	if (D) Log.d(TAG, "writeToAllDevices start...");
    	for (BluetoothSocket socket : mSocketConfig.getConnectedSocketList()) {
    	    synchronized (this) {
                //  if (mState != STATE_CONNECTED) return;
    	    	Log.d("tagsend", "∑¢ÀÕ"+Myhex.buffer2String(out));
    	    	writeToSocket(socket, out); 
    	    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
    	    	writeToSocket(socket, out); 
    	    	Log.e(TAG, "[writeToAllDevices] currentTimeMillis: "+ System.currentTimeMillis() );
            }
    	}
    }
    public void disconnectServerSocket() {
    	Log.d(TAG, "[disconnectServerSocket] ----------------");
    	/*
    	try {
    	    serverSocket.close();
            Log.w(TAG, "[disconnectServerSocket] Close "+serverSocket.toString());     
        } catch (IOException e) {
            Log.e(TAG, "close() of server failed", e);
    	}
    	*/
    	if (mServerSocketThread != null) {
    		mServerSocketThread.disconnect();
    	    mServerSocketThread = null; 
    	    Log.w(TAG, "[disconnectServerSocket] NULL mServerSocketThread"); 
    	}
    }
    public void disconnectSocketFromAddress(String address){
    	Set<BluetoothSocket> socketSets = mSocketConfig.containSockets(address);
    	for(BluetoothSocket socket : socketSets){
    		disconnectSocket(socket);
    	}
    }
    
    public synchronized void disconnectSocket(BluetoothSocket socket) {
    	Log.w(TAG, "[disconnectSocket] ------------------"+socket.toString()+" ; device name is "+socket.getRemoteDevice().getName());
    	if (!mSocketConfig.isSocketConnected(socket)){ //ÔøΩoÔøΩÃ©«©«™ÔøΩ...ÔøΩ_ÔøΩuÔøΩ…≥ÔøΩÔøΩ|ÔøΩ]ÔøΩiÔøΩÔøΩaaa (ÔøΩOÔøΩ]ÔøΩÔøΩÔøΩÔøΩÔøΩ∆∂]ÔøΩiÔøΩ”§FÔøΩ‚¶∏, ÔøΩƒ§GÔøΩÔøΩÔøΩN always ÔøΩiÔøΩÔøΩ, exception ÔøΩÔøΩÔøΩÔøΩY)
    		Log.w(TAG, "[disconnectSocket] mSocketConfig doesn't contain the socket: "+socket.toString()+" ; device name is "+socket.getRemoteDevice().getName() );
    		return;
    	}
    //	BluetoothSocket bluetoothSocket = mBluetoothSocekts.get(device).getBluetoothSocket();
    	Log.d(TAG, socket.getRemoteDevice().getName()+" connection was disconnected!");
    	//notifyUiFromToast(socket.getRemoteDevice().getName()+" connection was lost");
    	isLink = false;
        mSocketConfig.unregisterSocket(socket);
    }
    
    public void terminated(){
		Log.w(TAG, "[terminated] --------------");
		
		disconnectServerSocket();
    	for(BluetoothSocket socket : mSocketConfig.getConnectedSocketList()){
    		Log.w(TAG, "[terminated] Left Socket(s): "+mSocketConfig.getConnectedSocketList().size());
    		disconnectSocket(socket);
    	}
    	/*
    	if (mSocketConfig.getConnectedSocketList().size()>0) {
    		try {
    	        mBluetoothSocekts.clear();
    		}catch(UnsupportedOperationException e) {
    			Log.i(TAG, "[terminated] Clear Socket Map error.");
    		}
    	}
    	*/
    	Log.w(TAG, "[terminated] Final Left Socket(s): "+mSocketConfig.getConnectedSocketList().size());
    }
    /*
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        ConnectedThread tmpThread;
        int i = 0;
        for (BluetoothSocket socket : mBluetoothSocekts.keySet()) {
    	    synchronized (this) {
                //  if (mState != STATE_CONNECTED) return;
    	    	tmpThread = mBluetoothSocekts.get(socket);
    	    	if (tmpThread!= null) {
    	    	    tmpThread.cancel();
    	    	    ++i;
        	    	Log.w(TAG, "[stop] Close "+i+" socket(s)");
    	    	    tmpThread = null;
    	    	    Log.w(TAG, "[stop] Stop the thread");
    	    	}
    	    	mBluetoothSocekts.remove(socket);       	
    	    	
            }    	    
    	}      //  if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null; Log.w(TAG, "[stop] Killed mAcceptThread");}
        
        if (mServerSocketThread != null) {
        //	try {
        	    mServerSocketThread.cancel(); 
        	   // mServerSocketThread.thread.join(); 
        	    mServerSocketThread = null; 
        	    Log.w(TAG, "[stop] Killed mServerSocketThread");        	    
        //    }catch (InterruptedException e){
        //    	Log.w(TAG, "[stop] mServerSocketThread close error" + e);
        //    }
        }
        mBluetoothSocekts = null;
        
    }
    */
    private void notifyUiFromToast(String str) {
 //   	Log.d(TAG, "test123 " + str);
        Message msg = mHandler.obtainMessage(BluetoothConnController.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothConnController.TOAST, str);
        msg.setData(bundle);
        mHandler.sendMessage(msg);  
    }
    
    private class ServerSocketThread implements Runnable {
    	private BluetoothServerSocket mmServerSocket = null;
    	private Thread thread = null;
    	private boolean isServerSocketValid = false;
    //	private final ExecutorService pool;
    	public ServerSocketThread(){
    		this.thread = new Thread(this);
    		
    		BluetoothServerSocket serverSocket = null;    		
    		 try {
    			 Log.i(TAG, "[ServerSocketThread] Enter the listen server socket");
    			//issc
    			if (DeviceDependency.shouldUseSecure()) {
        			 serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, CUSTOM_UUID); 
				}
    			 else {
        			 serverSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, CUSTOM_UUID);
				}
    			 Log.i(TAG, "[ServerSocketThread] serverSocket hash code = "+serverSocket.hashCode());
    			 isServerSocketValid = true;

             } catch (IOException e) {
            	 Log.e(TAG, "[ServerSocketThread] Constructure: listen() failed", e);
                 e.printStackTrace();
                // notifyUiFromToast("Listen failed. Restart application again");                
                 	//notifyUiFromToast("≥¢ ‘÷ÿ∆Ù»Ìº˛");                
                 isLink = false;
                 isServerSocketValid = false;
                 mServerSocketThread = null;
                 //BluetoothConnService.this.startSession();
             }             
            mmServerSocket = serverSocket;

            if(mmServerSocket != null){
            	String serverSocketName = mmServerSocket.toString();
            	Log.i(TAG, "[ServerSocketThread] serverSocket name = "+serverSocketName);
            }else {
            	Log.i(TAG, "[ServerSocketThread] serverSocket = null");
            }
    	}    	
    	public void start(){
            this.thread.start();
    	}
    	
		
		public void run() {
			if (D) Log.d(TAG, "BEGIN ServerSocketThread " + this+", thread id = ");
			BluetoothSocket socket = null;
			
			while( isServerSocketValid ) {
				try {
					Log.i(TAG, "[ServerSocketThread] Enter while loop");
					Log.i(TAG, "[ServerSocketThread] serverSocket hash code = "+mmServerSocket.hashCode());				
                    socket = mmServerSocket.accept();  
                   
                    Log.i(TAG, "[ServerSocketThread] Got client socket");                    
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }
                
                if (socket!=null) {
                	 synchronized (BluetoothConnModel.this) {                		 
                		 Log.i(TAG, "[ServerSocketThread] "+socket.getRemoteDevice()+" is connected.");
                		 connected(socket);
                		/* 
                		 if (mServerSocketThread != null) {
                	    	mServerSocketThread = null; 
                	    	Log.w(TAG, "[ServerSocketThread] NULL mServerSocketThread"); 
                	     }
                		 */
                		 BluetoothConnModel.this.disconnectServerSocket();              		 
                		 break;                		 
                	 }
                }                
			}	
			Log.i(TAG, "[ServerSocketThread] break from while");
			BluetoothConnModel.this.startSession();
		}
		
        public void disconnect() {
            if (D) Log.d(TAG, "[ServerSocketThread] disconnect " + this);
            if(mmServerSocket==null) return;
            try {
            	Log.i(TAG, "[ServerSocketThread] disconnect serverSocket name = "+mmServerSocket.toString());
                mmServerSocket.close();
                Log.i(TAG, "[ServerSocketThread] mmServerSocket is closed.");
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }    	
        
    }
    
    private class SocketThread implements Runnable {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private Thread thread = null;
    //	private final ExecutorService pool;
    	public SocketThread(BluetoothDevice device){
    		this.thread = new Thread(this);    	    		 
    	    Log.i(TAG, "[SocketThread] Enter these server sockets");
    	    mmDevice = device;
    	    BluetoothSocket tmp = null;

    	        // Get a BluetoothSocket for a connection with the given BluetoothDevice
    	    try {
    	    	//issc
    	    	if (DeviceDependency.shouldUseSecure()) {
        	        tmp = device.createRfcommSocketToServiceRecord(CUSTOM_UUID);
				}
    	    	else {
        	    	tmp = device.createInsecureRfcommSocketToServiceRecord(CUSTOM_UUID);
				}
    	    	/*issc2.1*/
    	    	/*Method m = mmDevice.getClass().getMethod("createInsecureRfcommSocket",new Class[] { int.class });
    	    	tmp = (BluetoothSocket)m.invoke(mmDevice,6);*/
    	        Log.i(TAG, "[SocketThread] Constructure: Get a BluetoothSocket for a connection, create Rfcomm");
    	    } catch (Exception e) {
    	         Log.e(TAG, "create() failed", e);
    	    }
    	     mmSocket = tmp;	 
    	     
    	}
    	
    	public void start(){
            this.thread.start();
    	}
    	
		
		public void run() {
			if (D) Log.d(TAG, "BEGIN SocketThread" + this);
			mAdapter.cancelDiscovery();
			// Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
            	//issc
            	/*issc2.1
            	Method createBondMethod = mmDevice.getClass().getMethod("createBond");
		    	createBondMethod.invoke(mmDevice);*/
                mmSocket.connect();
                Log.i(TAG, "[SocketThread] Return a successful connection");
            } catch (Exception e) {
            	//notifyUiFromToast("Unable to connect device: "+mmDevice.getName());
            	mHandler.sendEmptyMessage(MainActivity2.handler_LinkFailure);
            	isLink = false;
            	Log.i(TAG, "[SocketThread] Connection failed", e);
                // Close the socket
            	
                try {
                    mmSocket.close();
                    Log.i(TAG, "[SocketThread] Connect fail, close the client socket");
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                } catch (Exception e1) {
                	e1.printStackTrace();
                }
                
            	//disconnectSocket(mmSocket);
                // Start the service over to restart listening mode
                //BluetoothChatService.this.start();
                this.thread = null;
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothConnModel.this) {
    		    connected(mmSocket);
                Log.i(TAG, "[SocketThread] "+mmDevice+" is connected.");
            }
            this.thread = null;
            if (D) Log.i(TAG, "END mConnectThread");

		}
		/*
        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
            	mmSocket.close();
                Log.i(TAG, "[SocketThread] mmSocket is closed.");
            } catch (IOException e) {
                Log.e(TAG, "[SocketThread] close() of client failed", e);
            }
        }  
        */  	
        
    }
    
    public class ConnectedThread implements Runnable {
    	protected BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
    	private Thread thread = null;

        
    	private ConnectedThread(BluetoothSocket socket) {
    		this.thread = new Thread(this, socket.getRemoteDevice().toString());
    		mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                Log.i(TAG, "[ConnectedThread] Constructure: Set up bluetooth socket i/o stream");
            } catch (IOException e) {
                Log.e(TAG, "[ConnectedThread] temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;      
            
    	}
    	    	
    	public void start(){
            this.thread.start();
    	}
		
		public void run() {
			if (D) Log.d(TAG, "BEGIN ConnectedThread" + this);
			byte[] buffer = new byte[1024];
            int bytes;
            mTxBytes = mRxBytes = 0;
            //int itmp = 0;
			while (mSocketConfig.isSocketConnected(mmSocket)) {
				/*try {
					if (D) Log.d(TAG, "Socket available");
				}
				catch(Exception e){
					mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1, "Exception during available()\n" + e).sendToTarget();
					disconnectSocket(mmSocket);
					Log.e(TAG, "Exception during available()\n", e);
					break;
					
				}*/
				try {
						//if (itmp != 0) {
			                   // Read from the InputStream
						//	Log.i(TAG, "read start");
		                    bytes = mmInStream.read(buffer, 0, 1024);
		                    //String msg = new String(buffer, 0, bytes, "ISO-8859-1");
		                    mRxBytes += bytes;
		                    
		                      Log.i(TAG, "[ConnectedThread] read bytes: "+bytes );
		                    ProcessDataCommand(buffer, bytes);
		                    
		                    
		                    
		               //     if (inStreamBuilder.length()+bytes<1024){                    	
		               //         inStreamBuilder.append(msg);                               
		                        
		                   //aa Log.i(TAG, "[ConnectedThread] Input message: "+msg );
		                   //aa Log.i(TAG, "[ConnectedThread] currentTimeMillis: "+ System.currentTimeMillis() );
		                    //  mHandler.obtainMessage(BluetoothConnController.MESSAGE_READ, bytes, -1, buffer).setTarget(mHandler);
		                        // Send the obtained bytes to the UI Activity
		              //      mHandler.obtainMessage(BluetoothConnController.MESSAGE_DEVICE_NAME, bytes, -1, mmSocket.getRemoteDevice())
		                //        .sendToTarget();
		                  //mHandler.obtainMessage(BluetoothConnController.MESSAGE_READ, bytes, mRxBytes, buffer).sendToTarget();
//		                    Message message= mHandler.obtainMessage();
//		                    message.what=MainActivity2.MESSAGE_READ;
//		                    message.arg1=bytes;
//		                    message.obj= buffer;
//		                    mHandler.sendMessage(message);
//		                    Log.i(TAG, "   " + Myhex.buffer2String(buffer, bytes));
		                    
		                    
		              //      } else {
//		                    	
//		                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
//		                        boolean isEcho = settings.getBoolean(MainActivity2.KEY_ECHO_PREF, false);
//		    	                if (isEcho){
//		    	                	
//		    	                	mTxBytes += bytes;
//		    	                	write(msg);//echo
//		    	                	Log.d(TAG, "write OK");
//		    	                }
//		    	            	if (mMonitor) {
//		    	            		try {
//		    	                		mOutputFile.write(msg.toString().getBytes());
//		    	                		mMonitorBytes+= bytes;
//		    	                
//		    	            		}
//		    	            		catch (Exception e) {
//		    	            			Log.e(TAG, "write FileOutputStream fail", e);
//		    	            			mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1, "write FileOutputStream fail\n" + e).sendToTarget();
//		    	            		}
//		    	            	}
						//}
 
                } catch (IOException e) {
                	
                	Log.e(TAG, "[ConnectedThread] connection lost", e);
                	//mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, 1, -1, "Exception during read()\n" + e).sendToTarget();
                	disconnectSocket(mmSocket);
                	Log.w(TAG, "[ConnectedThread] disconnect the socket");
                    //e.printStackTrace();
                	//notifyUiFromToast(mmSocket.getRemoteDevice().getName()+" was disconnected.");
                	mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1, mmSocket.getRemoteDevice().getName()+" was disconnected.").sendToTarget();
                	mHandler.sendEmptyMessage(MainActivity2.handler_linkLost);
                	isLink = false;
                    break;
                }
				
			}
		//	notifyUiFromToast("Socket is disconnected");
		//	if (!socketConfig.isSocketConnected()) {
		//		disconnectSocket(mmSocket.getRemoteDevice());
		///		Log.w(TAG, "[ConnectedThread] disconnect the socket");
		//	}
			if (D) Log.i(TAG, "[ConnectedThread] break from while");
			
		}
		
		
		
		
		public  int port_data_conmand_length;
		public byte[] buff ;
    	public void ProcessDataCommand(byte[] command, int length) {
    		Log.v("tag_rece", "old length:" + Myhex.buffer2String(command, length));
    		for (int index = 0; index < length; index++) {
    			if(index%3 !=0) {
    				index ++;
    				continue;
    			}
    			if(command[index]==(byte)0xE0) {
    				buff = new byte[6];
    				buff[0] = command[index];
        			buff[1] = command[index+1];
        			buff[2] = command[index+2];
        			buff[3] = command[index+3];
        			buff[4] = command[index+4];
        			buff[5] = command[index+5];
        			ProcessData(buff, 6);
        			index+=5;
        			continue;
    			} else if(command[index]!= (byte)0x00) {
    				buff = new byte[3];
    			} else {
    				return;
    			}
    			buff[0] = command[index];
    			buff[1] = command[index+1];
    			buff[2] = command[index+2];
    			ProcessData(buff, 3);
    			index+=2;
    		}
    	}
    	
    	private void ProcessData(byte[] buffer, int length) {
    		if(buffer==null) {
    			return;
    		}
    	      Message message= mHandler.obtainMessage();
              message.what=MainActivity2.MESSAGE_READ;
              message.arg1=length;
              message.obj= buffer;
              mHandler.sendMessage(message);
              Log.i(TAG, "   " + Myhex.buffer2String(buffer, length));
    	}
		
		
		
		
		
		public boolean write(String msg) {
            try {
            	mTxBytes += msg.length();
                mmOutStream.write(msg.toString().getBytes());
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BluetoothConnController.MESSAGE_WRITE, -1, mTxBytes, msg.toString().getBytes()).sendToTarget();                

            } catch (IOException e) {
                Log.e(TAG, "[ConnectedThread] Exception during write", e);
                mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, 1, -1, "Exception during write\n" + e).sendToTarget();
                return false;
            }
            return true;
        }
		
		public void write(byte[] buffer) {
			  try {
				mmOutStream.write(buffer);
				Log.d("tagsend", "∑¢ÀÕ2 ->"+Myhex.buffer2String(buffer));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
/*
        public void cancel() {
            try {
            	if (mmInStream != null){
            		mmInStream.close();
            	}
            	if (mmOutStream != null){
            		mmOutStream.close();
            	}
            	if (mmSocket != null) {
                    mmSocket.close();
            	    Log.w(TAG, "[ConnectedThread] close() bluetooth socket");
            	}
            } catch (IOException e) {
                Log.e(TAG, "[ConnectedThread] close() of connect socket failed", e);
            }
        }
        */
    	
    }
   public class SendFileThread extends ConnectedThread {
	   private String fileName;
    	private SendFileThread(BluetoothSocket socket, String file){
    		super(socket);
    		fileName = file;
    		Log.d(TAG, "SendFileThread Create: " + file);
    	}
		/* (non-Javadoc)
		 * @see com.wmlab.bluetoothconn.BluetoothConnModel.ConnectedThread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			FileInputStream inputStream;
			if (D) Log.d(TAG, "BEGIN SendFileThread " + this);
			try {
				inputStream = new FileInputStream(fileName);
			}
			catch(Exception e){
				Log.d(TAG, "Exception during new FileInputStream");
				return;
			}
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while (mSocketConfig.isSocketConnected(mmSocket)) {
				try {
					bytes = inputStream.read(buffer, 0, 1024);
					if (bytes <= 0) { //-1: EOF
						mHandler.obtainMessage(BluetoothConnController.MESSAGE_ALERT_DIALOG, -1, -1, "Send " + fileName + " completely").sendToTarget();
						break;
					}
					Log.d(TAG, "length = " + bytes);
					String msg = new String(buffer, 0, bytes, "ISO-8859-1");
					if (write(msg) == false) {
						break;
					}
					Log.d(TAG, "[send file]write OK");
				}
				catch(Exception e){
					Log.d(TAG, "[SendFile] Exception during send file", e);
					break;
				}
				
				
			}
		}
    	
    }
public void close() {
	// TODO Auto-generated method stub
	isLink = false;
}
	
}
