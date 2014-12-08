package com.gotye.api;

/**
 * 操作会回调状态码
 * @author Administrator
 *
 */
public class GotyeStatusCode {
	    /**
	     * async calling sucessfully
	     */
	    public static  final int CODE_WAITF_OR_CALLBACK = -1;        
		
	    /**
	     * sync calling sucessfully
	     */
	    public static final int CODE_OK = 0;                      
		
	    /**
	     * system busy
	     */
	    public static final int CODE_SYSTEM_BUSY = 1;                 
	    
	    /**
	     *  not logged in yet
	     */
	    public static final int CODE_NOT_LOGIN = 2;                 
	    
	    /**
	     * create file failed
	     */
	    public static final int CODE_CREATE_FILE_FAILED = 3;           
	    
	    /**
	     * target is self
	     */
	    public static final int CODE_TARGET_IS_SELF = 4;               
	    
	    /**
	     * room not exist
	     */
	    public static final int CODE_ROOM_NOT_EXIST = 33;          
	    
	    /**
	     * room is full
	     */
	    public static final int CODE_ROOM_IS_FULL = 34;            

	    /**
	     * not in the room
	     */
	    public static final int CODE_NOT_IN_ROOM = 35;             
	    
	    
	    /**
	     * forbidden
	     */
	    public static final int CODE_FORBIDDEN = 36;              
	    
	    /**
	     *  time out
	     */
	    public static final int CODE_TIMEOUT =300;              
		
	    /**
	     * verification failed
	     */
	    public static final int CODE_VERIFYFAILED = 400;         
	    
	    /**
	     * no permission
	     */
	    public static final int CODE_NO_PERMISSION = 401;
	    
	    /**
	     * repeatoper
	     */
	    public static final int CODE_REPEATOPER = 402;
	    
	    /**
	     * group not found
	     */
	    public static  final int CODE_GROUP_NOT_FOUND = 403;
	    
	    /**
	     * user not found
	     */
	    public static final int CODE_USER_NOT_FOUND = 404;
	    
	    /**
	     * login failed
	     */
	    public static final int CODE_LOGIN_FAILED = 500;         
		
	    /**
	     * your account has logged in another device.
	     */
	    public static final int CODE_FORCELOGOUT = 600;          
		
	    /**
	     * network disconnected
	     */
		public static final int CODE_NETWORD_DISCONNECTED = 700;     
	    
		/**
		 *  friend not exist
		 */
		public static final int CODE_USER_NOT_EXIST = 804;           
	    
		/**
		 * requesting mic failed
		 */
		public static final int CODE_REQUEST_MIC_FAILED= 806;     
	    
		/**
		 * recording time over
		 */
		public static  final int CODE_VOICE_TIME_OVER = 807;         
	    
		/**
		 * recording device is in use
		 */
		public static final int CODE_RECORDER_BUSY = 808;         
	    
		/**
		 * parameters invalid
		 */
		public static final int CODE_INVALIDARGUMENT = 1000;      
	    
		/**
		 * server error
		 */
		public static final int CODE_SERVER_PROCESS_ERROR = 1001;     
	    
		/**
		 * unknown error
		 */
		public static final int CODE_UNKNOW_ERROR = 1100;       

}
