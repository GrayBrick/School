import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class algoritm {

    public static ArrayList<ArrayList<Part>>    field = new ArrayList<>();
    public static byte                          build_type = 0;     //0 = по часовой
                                                                    //1 = против
    public static byte                          type_evr = 0;       //0 = эвристика a + b
                                                                    //1 = выбирает путь ближе к стенке
                                                                    //2 = по пифагору
    public static int                           size_map = 3;
    public static byte                          type_algo = 0;      //0 = A_star
                                                                    //1 = волной
                                                                    //2 = жадный поиск
    public static byte                          type_output = 0;    //0 = просто выводит финальное решение если оно есть
                                                                    //1 = выводит каждый шаг
                                                                    //2 = выводит поиск путей
    public static boolean                       show_color = false;

    public static boolean                       run_collect = true;

    public static void main(String[] args)
    {
        read_args(args);
        solve(size_map);
    }

    public static void check_map_line(String line) {
        int count = 0;
        line = line.split("#")[0].trim();
        String[] parseLine = line.split(" ");
        for (int i = 0; i < parseLine.length; i++) {
            if (parseLine[i].length() == 0)
                continue;
            if (!isOnlyDigits(parseLine[i]))
                error_message("only numbers!");
            count++;
        }
        if (count != size_map)
            error_message("invalid map!");
    }

    public static boolean read_map_size(String line) {
        if (line == null)
            error_message("invalid map!");
        line = line.split("#")[0].trim();
        if (line.length() == 0)
            return true;
        size_map = Integer.parseInt(line);
        if (!isOnlyDigits(line))
            error_message("invalid size map!");
        if (size_map < 0)
            error_message("negative size map!");
        return false;
    }

    public static boolean read_map(String map) {
        try (FileReader reader = new FileReader(map))
        {
            String line;
            BufferedReader bufferReader = new BufferedReader(reader);
            List<String> stack = new ArrayList<>();
            while(read_map_size(bufferReader.readLine()))
                continue;
            while ((line = bufferReader.readLine()) != null)
            {
                check_map_line(line);
                stack.add(line);
            }
            if (stack.size() != size_map)
                error_message("invalid map!");
            return fill_field(stack);
        }
        catch (IOException ex) {error_message(ex.getMessage());}
        return false;
    }

    public static void read_args(String[] args)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-h") || args[i].equals("-help"))
                show_help();
            else if (args[i].equals("-c") || args[i].equals("-color"))
                show_color = true;
            else if (args[i].equals("-o") || args[i].equals("-output"))
                type_output = parse_args(args[i], args[i + 1], 2);
            else if (args[i].equals("-a") || args[i].equals("-alg"))
                type_algo = parse_args(args[i], args[i + 1], 2);
            else if (args[i].equals("-b") || args[i].equals("-build"))
                build_type = parse_args(args[i], args[i + 1], 2);
            else if ((args[i].equals("-s") || args[i].equals("-size")) && field.size() == 0)
                size_map = parse_args(args[i], args[i + 1], 150);
            else if (args[i].equals("-e"))
                type_evr = parse_args(args[i], args[i + 1], 2);
            else if (args[i].equals("-m") || args[i].equals("-map"))
                if (!read_map(args[i + 1]))
                    error_message("something went wrong");
        }
    }

    public static void error_message(String message)
    {
        System.out.print(ChatColor.RED + "Error: ");
        System.out.println(message);
        System.exit(1);
    }

    public static byte parse_args(String flag, String arg, int max_value)
    {
        if (!isOnlyDigits(arg))
            error_message("only numbers!");
        final byte value = (byte)Integer.parseInt(arg);
        if (value > max_value || value < 0)
            error_message(flag + " min value 0, max value " + max_value);
        return (value);
    }

    private static boolean isOnlyDigits(String str) {
        return str.matches("[\\d]+");
    }

    public static void show_help() {
        System.out.println("flags:");
        System.out.println("-b, -build [0-2]: this is build type. Default value 0");
        System.out.println("-a, -alg [0-2]: this is  type. Default value 0");
        System.out.println("-o, -output [0-2]: this is output type Default value 0");
        System.out.println("-s, -size [0-150]: map size (the limit 150 is set so as not to load the computer)");
        System.out.println("-c, -color: use colors");
        System.exit(1);
    }

    public static void solve(int size)
    {
        Date d1 = new Date();
        List<Integer> list_value = new ArrayList<>();

        if (field.size() != size_map && field.size() == 0)
            fill_field_random(list_value, size);

        if (!no_rep())
        {
            System.out.println("Элементы повторяются или их недостаточно");
            return ;
        }

        if (!isCollect())
        {
            System.out.println("не соберется");
            return ;
        }
        run_collect = false;

        if (build_type != 2)
            for (int i = 1; i < size * size; i++)
            {
                if (algos(i))
                    continue ;
                break ;
            }
        else
            for (int i = (size * size) - 1; i > 0; i--)
            {
                if (algos(i))
                    continue ;
                break ;
            }

        print_field();
        System.out.println("Всего передвижений: " + Part.all_move);
        Date d2 = new Date();
        System.out.println("Времени затрачено: " + (d2.getTime() - d1.getTime()) + " mc");
    }

    public static boolean no_rep()
    {
        ArrayList<Integer> values = new ArrayList<>();

        for (ArrayList<Part> parts : field)
        {
            for (Part part : parts)
            {
                if (values.contains(part.value))
                    return false;
                values.add(part.value);
            }
        }
        if (values.size() != Math.pow(size_map, 2))
            return false;
        return true;
    }

    public static boolean algos(int i)
    {
        if (i == (build_type != 2 ? (Part.size * Part.size) - 1 : 1))
        {
            if (Part.get_part(i).get_final_position().equals(Part.get_part(i).position))
            {
                Part.get_part(i).block = true;
                return false;
            }
            else
            {
                Part.get_part(i).move();
                if (Part.get_part(i).position.equals(Part.get_part(i).get_final_position()))
                {
                    Part.get_part(i).block = true;
                    return true;
                }
                return false;
            }
        }
        if (Part.get_part(i).block)
            return true;
        if (Part.get_part(Part.get_part(i).get_final_position()).isSpecial())
        {
            if (Part.get_part(i).position == Part.get_part(i).get_final_position() &&
                Part.get_part(i + 1).position == Part.get_part(i + 1).get_final_position())
            {
                Part.get_part(i).block = true;
                Part.get_part(i + 1).block = true;
                return true;
            }
            solve_special_part(Part.get_part(i), Part.get_part(i).get_final_position());
        }
        else
            move_from_to(Part.get_part(i), Part.get_part(i).get_final_position(), true);
        return true;
    }

    public static boolean solve_special_part(Part from, int[] position)
    {
        Part next = Part.get_part(from.value + (build_type == 2 ? -1 : 1));

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
        if (to == null || !move_from_to(from, to.position, false))
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
                if (part.block)
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

    public static boolean fill_field(List<String> map)
    {
        Part.set_size(size_map);

        int i = 0;
        int j;
        for (String line : map)
        {
            String []values = line.split(" ");
            field.add(new ArrayList<>());
            j = 0;
            for (String value : values)
            {
                try
                {
                    int value_int = Integer.parseInt(value);
                    field.get(i).add(new Part(value_int, new int[]{i, j++}));
                } catch(Exception ex){continue;}
            }
            i++;
        }
        return true;
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

    public static ArrayList<ArrayList<Part>> clone_field()
    {
        ArrayList<ArrayList<Part>>  clone_field = new ArrayList<>();

        for (int i = 0; i < algoritm.field.size(); i++)
        {
            clone_field.add(new ArrayList<>());
            for (int j = 0; j < algoritm.field.get(i).size(); j++)
            {
                clone_field.get(i).add(new Part(field.get(i).get(j).value, field.get(i).get(j).position));
            }
        }

        return clone_field;
    }

    public static boolean isCollect()
    {
        byte buf = type_output;
        type_output = 0;
        int size = Part.size;
        ArrayList<ArrayList<Part>> clone_field = clone_field();

        if (build_type != 2)
        {
            for (int i = 1; i < size * size; i++)
            {
                if (algos(i))
                    continue ;
                break ;
            }
        }
        else
        {
            for (int i = (size * size) - 1; i > 0; i--)
            {
                if (algos(i))
                    continue ;
                break ;
            }
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
