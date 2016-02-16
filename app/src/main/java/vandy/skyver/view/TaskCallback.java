/* 
**
** Copyright 2014, Jules White
**
** 
*/
package vandy.skyver.view;

public interface TaskCallback<T> {

    public void success(T result);

    public void error(Exception e);

}
