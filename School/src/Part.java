import java.util.ArrayList;

public class Part
{
    static int              size;
    static int              all_move;

    int                     value;
    int[]                   position;
    boolean                 block;

    int                     weight = 0;
    Part                    dir;
    boolean                 close = false;

    boolean                 open = false;
    boolean                 move = false;


    public  Part(int value, int[] position)
    {
        this.block = false;
        this.value = value;
        this.position = position;
    }

    public boolean isSpecial()
    {
        for (Part part : getNear())
        {
            if (part.block)
                continue ;
            int open = 0;
            for (Part near : part.getNear())
            {
                if (!near.block)
                    open++;
            }
            if (open < 3)
                return true;
        }
        return false;
    }

    public boolean move()
    {
        if (block)
            return false;
        Part zero = null;
        for (Part part : getNear())
        {
            if (part.value == 0)
            {
                zero = part;
                break ;
            }
        }
        if (zero == null)
            return false;
        algoritm.field.get(this.position[0]).set(this.position[1], zero);
        algoritm.field.get(zero.position[0]).set(zero.position[1], this);
        int[] buf = this.position;
        this.position = zero.position;
        zero.position = buf;
        all_move++;
        if (algoritm.type_output != 0)
            algoritm.print_field();
        return true;
    }

    public ArrayList<Part> getNear()
    {
        ArrayList<Part> near = new ArrayList<>();

        if (this.position[0] != 0)
            near.add(algoritm.field.get(this.position[0] - 1).get(this.position[1]));
        if (this.position[0] != Part.size - 1)
            near.add(algoritm.field.get(this.position[0] + 1).get(this.position[1]));
        if (this.position[1] != 0)
            near.add(algoritm.field.get(this.position[0]).get(this.position[1] - 1));
        if (this.position[1] != Part.size - 1)
            near.add(algoritm.field.get(this.position[0]).get(this.position[1] + 1));
        return near;
    }

    public int[] get_final_position()
    {
        if (value == 1)
            return new int[]{0,0};
        if (value == 0)
            return position;
        Part ret_pos = algoritm.build_type == 0 ?
                algoritm.getFirstClockWise(
                        Part.get_part(value - 1)
                ) :
                algoritm.getFirstCounterClockWise(
                        Part.get_part(value - 1)
                );
        if (ret_pos != null)
            return ret_pos.position;
        return position;
    }

    public int distance(Part to)
    {
        int a = Math.abs(position[0] - to.position[0]);
        int b = Math.abs(position[1] - to.position[1]);

        return a + b;
    }

    public static Part get_part(int value)
    {
        int []position = get_position(value);
        if (position == null)
            return null;
        return algoritm.field.get(position[0]).get(position[1]);
    }

    public static Part get_part(int[] position)
    {
        if (position[0] < 0 || position[0] > Part.size - 1 || position[1] < 0 || position[1] > Part.size - 1)
             return null;
        return algoritm.field.get(position[0]).get(position[1]);
    }

    public static int[] get_position(int value)
    {
        for (int i = 0; i < algoritm.field.size(); i++)
        {
            for (int j = 0; j < algoritm.field.get(i).size(); j++)
            {
                if (algoritm.field.get(i).get(j).value == value)
                    return algoritm.field.get(i).get(j).position;
            }
        }
        return null;
    }

    public static void set_size(int size)
    {
        Part.size = size;
    }
}