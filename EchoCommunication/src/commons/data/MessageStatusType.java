package commons.data;

public enum MessageStatusType {
	ECHO,
	ECHO_SUCCESS,
	GET, /* Get - request */
	GET_ERROR, /* requested tuple (i.e. value) not found */
	GET_SUCCESS, /* requested tuple (i.e. value) found */
	PUT, /* Put - request */
	PUT_SUCCESS, /* Put - request successful, tuple inserted */
	PUT_UPDATE, /* Put - request successful, i.e. value updated */
	DELETE_SUCCESS,
	SERVER_AT_WRITE_LOCK,      /* Server locked for out, only get possible */
	SERVER_NOT_RESPONSIBLE_FOR_GET,
	SERVER_NOT_RESPONSIBLE_FOR_PUT,  /* Request not successful, server not responsible for key */
	PARAMETER_ERROR,
	SERVER_STOPPED,         /* Server is stopped, no requests are processed */
	START,
	UPDATE_META_INFO,
	DELETE_OLD_DATA,
	SET_WRITE_LOCK,
	RELEASE_WRITE_LOCK,
	ADD_MOVE_DATA,
	REMOVE_MOVE_DATA,
	ADD_DATA_MOVED,
	REMOVE_DATA_MOVED,
	STOP,
	SHUTDOWN_SERVER
}
