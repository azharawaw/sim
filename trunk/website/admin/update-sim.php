<?php

    include_once("password-protection.php");
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("sim-utils.php");
    
    function verify_mysql_result($result, $statement) {
        if (!$result && $statement !== "") {
            $message  = 'Invalid query: ' . mysql_error() . "\n";
            $message .= 'Whole query: ' . $statement;

            die($message);
        }
    }

    $update_simulation_st = "UPDATE `simulation` SET ";
    
    $sim_id = $_REQUEST['sim_id'];
    
    $is_first_entry = true; 

    foreach($_REQUEST as $key => $value) {
        if (preg_match('/sim_.*/', $key) == 1 && $key !== "sim_id") {
            $escaped_value = mysql_escape_string($value);

            if ($is_first_entry) {
                $is_first_entry = false;
            }
            else {
                $update_simulation_st = "$update_simulation_st, ";
            }
            
            $update_simulation_st = "$update_simulation_st `$key`='$escaped_value' ";

        }
        else {
            print "$key -> $value\n";
        }
    }

    $update_simulation_st = "$update_simulation_st WHERE `sim_id`='$sim_id'";

    verify_mysql_result(mysql_query($update_simulation_st, $connection), $update_simulation_st);
    
    $select_categories_st = "SELECT * FROM `category` ";
    $category_rows        = mysql_query($select_categories_st, $connection);
    
    while ($category_row=mysql_fetch_array($category_rows)) {
        $cat_id   = $category_row[0];
        $cat_name = $category_row[1];
        
        $key_to_check_for = "checkbox_cat_id_$cat_id";

        $statement             = "";
        $should_be_in_category = isset($_REQUEST["$key_to_check_for"]);
               
        $delete_statement = "DELETE FROM `simulation_listing` WHERE `cat_id`='$cat_id' AND `sim_id`='$sim_id' ";
               
        verify_mysql_result(mysql_query($delete_statement), $delete_statement);
                
        if ($should_be_in_category) {
            $statement = "INSERT INTO `simulation_listing` (`sim_id`, `cat_id`) VALUES('$sim_id', '$cat_id')";
        }
        
        print "Statement = $statement<br/>";
        
        $result = mysql_query($statement, $connection);
        
        verify_mysql_result($result, $statement);
    }    
    
    print "Your simulation update was successful.";

    print "<META http-equiv=\"Refresh\" content=\"2;url=edit-sim.php?sim_id=$sim_id\">";
?>