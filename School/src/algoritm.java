import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class algoritm {

    public static ArrayList<ArrayList<Part>>    field = new ArrayList<>();
    public static byte                          build_type = 0;
    public static byte                          type_algo = 0;      //0 = A_star
                                                                    //1 = волной
                                                                    //2 = жадный поиск
    public static byte                          type_output = 0;    //0 = просто выводит финальное решение если оно есть
                                                                    //1 = выводит каждый шаг
                                                                    //2 = выводит поиск путей
    public static boolean                       show_color = false;

    public static void main(String[] args)
    {
        for (int i = 0; i < 200; i++)
        {
            solve((int) (Math.random() * 50));
        }
    }

    public static void solve(int size)
    {
        Date d1 = new Date();
        field = new ArrayList<>();
        List<Integer> list_value = new ArrayList<>();

        build_type = 1;
        type_algo = 2;
        type_output = 0;
        show_color = true;

        fill_field_random(list_value, size);

        if (!isCollect())
        {
            System.out.println("не соберется");
            return ;
        }


        for (int i = 1; i < size * size; i++)
        {
            if (i == (size * size) - 1 && Part.get_part(i).get_final_position().equals(Part.get_part(i).position))
            {
                Part.get_part(i).block = true;
                break ;
            }
            if (Part.get_part(i).block)
                continue ;
            if (Part.get_part(Part.get_part(i).get_final_position()).isSpecial())
                solve_special_part(Part.get_part(i), Part.get_part(i).get_final_position());
            else
                move_from_to(Part.get_part(i), Part.get_part(i).get_final_position(), true);
        }
        print_field();
        System.out.println("Всего передвижений: " + Part.all_move);
        Date d2 = new Date();
        System.out.println("Времени затрачено: " + (d2.getTime() - d1.getTime()) + " mc");
    }

    public static boolean solve_special_part(Part from, int[] position)
    {
        Part next = Part.get_part(from.value + 1);

        if (next == null)
            return false;
        Part to = getFreePart(from.value, next.value);
        if (to == null)
            return false;
        if (!move_from_to(from, to.position, false))
            return false;
        if (!move_from_to(next, position, false))
            return false;
        next.move = true;
        to = build_type == 0 ? getFirstCounterClockWise(next) : getFirstClockWise(next);
        if (!move_from_to(from, to.position, false))
            return false;
        from.move = true;
        to = build_type == 0 ? getFirstClockWise(next) : getFirstCounterClockWise(next);
        if (!move_from_to(next, to.position, true))
            return false;
        from.move();
        from.block = true;
        return true;
    }

    public static Part getFirstCounterClockWise(Part part)
    {
        Part ret = Part.get_part(new int[]{part.position[0] + 1, part.position[1]});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0], part.position[1] - 1});
            if (close == null || close.block)
                return ret;
        }

        ret = Part.get_part(new int[]{part.position[0], part.position[1] - 1});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0] - 1, part.position[1]});
            if (close == null || close.block)
                return ret;
        }

        ret = Part.get_part(new int[]{part.position[0] - 1, part.position[1]});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0], part.position[1] + 1});
            if (close == null || close.block)
                return ret;
        }

        ret = Part.get_part(new int[]{part.position[0], part.position[1] + 1});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0] + 1, part.position[1]});
            if (close == null || close.block)
                return ret;
        }
        return null;
    }

    public static Part getFirstClockWise(Part part)
    {
        Part ret = Part.get_part(new int[]{part.position[0] + 1, part.position[1]});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0], part.position[1] + 1});
            if (close == null || close.block)
                return ret;
        }

        ret = Part.get_part(new int[]{part.position[0], part.position[1] - 1});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0] + 1, part.position[1]});
            if (close == null || close.block)
                return ret;
        }

        ret = Part.get_part(new int[]{part.position[0] - 1, part.position[1]});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0], part.position[1] - 1});
            if (close == null || close.block)
                return ret;
        }

        ret = Part.get_part(new int[]{part.position[0], part.position[1] + 1});
        if (ret != null && !ret.block && !ret.move)
        {
            Part close = Part.get_part(new int[]{part.position[0] - 1, part.position[1]});
            if (close == null || close.block)
                return ret;
        }
        return ret;
    }

    public static Part getFreePart(int value_a, int value_b)
    {
        int value = -1;

        for (ArrayList<Part> parts : field)
        {
            for (Part part : parts)
            {
                if (part.block ||
                        part.value == value_a ||
                        part.value == value_b ||
                        part.value == 0)
                    continue ;
                if (value == -1)
                    value = part.value;
                else
                    if (Part.get_part(value).distance(Part.get_part(Part.get_part(value_a).get_final_position())) < part.distance(Part.get_part(Part.get_part(value_a).get_final_position())))
                        value = part.value;
            }
        }
        if (value != -1)
            return Part.get_part(value);
        return null;
    }

    public static boolean move_from_to(Part from, int[] position, boolean block)
    {
        Part to = Part.get_part(position);
        from.move = true;
        if (!A_star.start_find_way(from, to))
            return false;
        ArrayList<int[]> way = A_star.getWayPosition(from, to);
        for (int []part_position : way)
        {
            Part zero = Part.get_part(0);
            to = Part.get_part(part_position);
            if (!A_star.start_find_way(zero, to))
                return false;
            A_star.do_way(A_star.getWay(zero, to));
            from.move();
        }
        from.block = block;
        if (!block)
            from.move = false;
        return true;
    }

    public static void fill_field_random(List<Integer> list_value, int size)
    {
        Part.set_size(size);

        for (int i = 0; i < size; i++)
        {
            field.add(new ArrayList<>());
            for (int i1 = 0; i1 < size; i1++)
            {
                field.get(i).add(new Part(get_rand_value(list_value, size), new int[]{i, i1}));
            }
        }
    }

    public static int get_rand_value(List<Integer> list_value, int size)
    {
        int ret;

        while (true)
        {
            ret = (int) (Math.random() * (size * size));
            if (!list_value.contains(ret))
            {
                list_value.add(ret);
                return ret;
            }
        }
    }

    public static void print_field()
    {
        String field_str = "";

        for (ArrayList<Part> part_list : field)
        {
            if (field_str.split("").length != 0)
                field_str += "\n";
            for (Part part : part_list)
            {
                if (algoritm.show_color)
                {
                    if (part.block)
                        field_str += ChatColor.WHITE_BRIGHT + ChatColor.RED_BACKGROUND +
                                (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value)
                                + " " + ChatColor.RESET;
                    else if (part.move)
                    {
                        field_str += ChatColor.WHITE_BRIGHT + ChatColor.YELLOW_BACKGROUND +
                                (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value)
                                + " " + ChatColor.RESET;
                    }
                    else if (part.value == 0)
                    {
                        field_str += ChatColor.WHITE_BRIGHT + ChatColor.CYAN_BACKGROUND +
                                (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value)
                                + " " + ChatColor.RESET;
                    }
                    else if (part.open)
                        field_str += ChatColor.WHITE_BRIGHT + ChatColor.GREEN_BACKGROUND +
                                (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value)
                                + " " + ChatColor.RESET;
                    else if (part.close)
                        field_str += ChatColor.WHITE_BRIGHT + ChatColor.BLACK_BACKGROUND +
                                (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value)
                                + " " + ChatColor.RESET;
                    else
                        field_str += (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value) + " ";
                }
                else
                    field_str += (part.value < 1000 ? (part.value < 100 ? (part.value < 10 ? ("   " + part.value) : ("  " + part.value)) : " " + part.value) : part.value) + " ";
            }
        }
        field_str += "\n";

        System.out.print(field_str);
    }

    public static boolean isCollect()
    {
        byte buf = type_output;
        type_output = 0;
        int size = Part.size;
        ArrayList<ArrayList<Part>> clone_field = (ArrayList<ArrayList<Part>>) field.clone();

        for (int i = 1; i < size * size; i++)
        {
            if (i == (size * size) - 1 && Part.get_part(i).get_final_position().equals(Part.get_part(i).position))
            {
                Part.get_part(i).block = true;
                break ;
            }
            if (Part.get_part(i).block)
                continue ;
            if (Part.get_part(Part.get_part(i).get_final_position()).isSpecial())
                solve_special_part(Part.get_part(i), Part.get_part(i).get_final_position());
            else
                move_from_to(Part.get_part(i), Part.get_part(i).get_final_position(), true);
        }
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if (field.get(i).get(j).value != 0 && !field.get(i).get(j).block)
                    return false;
            }
        }
        field = clone_field;
        type_output = buf;
        return true;
    }
}
