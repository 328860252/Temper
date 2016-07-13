/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.medical.agreement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatManager {
    // Debugging
    private static final String TAG = "HomeSecurityService";
    private static final boolean D = true;
//    public static String type;
//    public static final String Link="link";
//    public static final String Cut="cut";
      private  boolean isLink=false;
    // Name for the SDP record when creating server socket
    private static final String NAME = "HomeSecurity";

    // Unique UUID for this application
   // private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	//00001101-0000-1000-8000-00805F9B34FB
    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 20;       // we're doing nothing
    public static final int STATE_LISTEN = 21;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 22; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 23;  // now connected to a remote device
    public final static int Link_Lost=24;
    /**
     * Constructor. Prepares a new HomeSecurity session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothChatManager(Context context, Handler handler) {
    	isLink=false;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
    //    mHandler.obtainMessage(BodyHeight.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    //�õ�Ҫ���ӵ������豸������ �ͷ���
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(BodyHeight.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(BodyHeight.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
      setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
      
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
       
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);
        Message m=mHandler.obtainMessage();
        m.what=ConnectionInterface.LinkFailure;
        mHandler.sendMessage(m);
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BodyHeight.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BodyHeight.TOAST, "���Ӳ��������豸");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
    }

    public static  boolean promptLost=true;
    private void connectionLost() {
        if(!promptLost){
        	promptLost=true;
        	return;
        }
    	
    	setState(STATE_LISTEN);

        setState(STATE_LISTEN);
        Message m=mHandler.obtainMessage();
        m.what=Link_Lost;
        mHandler.sendMessage(m);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatManager.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();//ȡ����ҵĽ��

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
            	Message m=mHandler.obtainMessage();
                m.what=ConnectionInterface.Link;
                mHandler.sendMessage(m);
                
                Log.v(TAG,"is linking");
                mmSocket.connect();
                isLink=true;
                Log.v(TAG,"link success");
               
                
                Message m2=mHandler.obtainMessage();
                m2.what=ConnectionInterface.LinkSuccess;
                mHandler.sendMessage(m2);
                
			
               
            } catch (IOException e) {

				isLink = false;

				// 第二次连接
				try {
					Thread.sleep(1000);
					Message m = mHandler.obtainMessage();
					m.what = ConnectionInterface.Link;
					mHandler.sendMessage(m);

					 Log.v(TAG,"第二次正在连接");
					mmSocket.connect();
					isLink = true;
					 Log.v(TAG,"第二次连接成功");

					Message m2 = mHandler.obtainMessage();
					m2.what = ConnectionInterface.LinkSuccess;
					mHandler.sendMessage(m2);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (!isLink) {
						connectionFailed();
						// Close the socket
						try {
							mmSocket.close();
						} catch (IOException e2) {
							Log.e(TAG,
									"unable to close() socket during connection failure",
									e2);
						}
						// Start the service over to restart listening mode
						// BluetoothChatManager.this.start();
						return;
					}
				}

			
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatManager.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
            	if(mmSocket!=null) {
            		mmSocket.close();
            	}
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[256];
            int bytes;

            // Keep listening to the InputStream while connected
            byte[] buf;
            while (true) {
                try {
                    // Read from the InputStream
                	buffer = new byte[256];
                    bytes = mmInStream.read(buffer);
                    buf = new byte[bytes];
                    System.arraycopy(buffer, 0, buf, 0, bytes);
                    Message msg = mHandler.obtainMessage();
                    msg.what = ConnectionInterface.GetData;
                    msg.obj = buf;
                    mHandler.sendMessage(msg);
                    //ProcessDataCommand(buffer,bytes);
                } catch (IOException e) {
                	e.printStackTrace();
                    isLink=false;
                    connectionLost();
                    break;
                }

            }
        }
        
      
    	public  int port_data_conmand_length;
    	public void ProcessDataCommand(byte[] command, int length) {
    		Log.v("data", "old length:" + length);
//    		for (int index = 0; index < length; index++) {
//    			if (command[index] == (byte) 0x7e) { // 
//    				port_data_conmand = null;
//    				port_data_conmand = new byte[256];
//    				port_data_conmand[0] = (byte) 0x7e;
//    				port_data_conmand_length = 1;
//
//    			} else if (command[index] == (byte) 0x7A) {// �յ�β��
//    				port_data_conmand[port_data_conmand_length] = (byte) 0x7a;// ���Ԫ��?    				port_data_conmand_length++;
//    				ProcessData(port_data_conmand, port_data_conmand_length);// ����
//    				port_data_conmand_length = 0;
//    				
//
//    			} else { // �յ�������
//    				if (port_data_conmand_length != 0) {
//    					port_data_conmand[port_data_conmand_length] = command[index];// ���Ԫ��?    					port_data_conmand_length++;
//    				}
//    			}
//    			
//    		}
    	}
    	//�ϳ�0x70
    	public  void ProcessData(byte[] message, int length) {

    		byte[] data = new byte[length];
    		for (int i = 0, j = 0; i < length; i++, j++) {
    			if (message[i] == (byte) 0x70) {
    				data[j] = (byte) (message[i] + message[i + 1]);
    				i++;
    			} else {
    				data[j] = message[i];
    			}
    		}
//    		 Message m=mHandler.obtainMessage();
//    		 m.what=ECookerActivity.Incept_Data;
//             m.obj=data; 
//         //    m.arg1=bytes;
//             mHandler.sendMessage(m);
    	}
        
        

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
        	if(mmOutStream==null) {
        		Log.d(TAG, "BluetoothChatManager.write   mmOutStream  is null");
        		return;
        	}
            try {
                mmOutStream.write(buffer);
                
                try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
             //   mHandler.obtainMessage(BodyHeight.MESSAGE_WRITE, -1, -1, buffer)
              //          .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
    boolean islink() {
    	return isLink;
    }
    
}
