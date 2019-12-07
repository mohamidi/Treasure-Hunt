package treasure_hunt;
import java.io.Serializable;

public class MyGameOutput  implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    MyGame myGame =null;   
    public MyGameOutput(MyGame gs)
    {
        myGame =gs;
    }
}