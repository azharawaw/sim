<?php
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("sim-utils.php");
    include_once("db-utils.php");    
    include_once("sys-utils.php");
    include_once("authentication.php");
    
    include_once("../teacher_ideas/referrer.php");
    
    // Don't want browsers caching the javascript code:
    expire_page_immediately();
    
    function print_header_navigation_element($prefix, $selected_page, $link, $desc, $access_key) {
        $this_element_is_selected = "$access_key" == "$selected_page";

        if ($this_element_is_selected) {
            $selected_status = "class=\"selected\"";
        }
        else {
            $selected_status = '';
        }

        print <<<EOT
            <li $selected_status><a href="$prefix/$link" accesskey="$access_key">$desc</a></li>
EOT;
    }
    
    function print_navigation_element($prefix, $selected_page, $link, $desc, $submenu_text) {
        static $access_key = 1;
        
        $this_element_is_selected = "$access_key" == "$selected_page";
        
        if ($this_element_is_selected) {
            $selected_status = "class=\"selected\"";
        }
        else {
            $selected_status = '';
        }
        
        print <<<EOT
            <li $selected_status><a href="$prefix/$link" accesskey="$access_key">$desc</a></li>
EOT;

        if ($this_element_is_selected) {
            print $submenu_text;
        }

        $access_key = $access_key + 1;
    }
    
    function get_sim_categories_html($prefix) {
        global $connection;
        
        $categories = '';

        // List all the categories:

        // start selecting SIMULATION CATEGORIES from database table
        $category_table = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);

        while ($category = mysql_fetch_assoc($category_table)) {
            $cat_id   = $category['cat_id'];
            $cat_name = format_for_html($category['cat_name']);

            $categories .= "<li class=\"sub\"><span class=\"sub-nav\">".
                           "<a href=\"${prefix}/simulations/index.php?cat=$cat_id\">&rarr; $cat_name</a></span></li>";          
        } 
        
        return $categories;
    }
    
    function print_navigation_bar($selected_page = null, $prefix = "..") {    
        print <<<EOT
            <div id="localNav">
                <ul>
EOT;

        print_navigation_element($prefix, $selected_page, "index.php",                 "Home",
            ''
        );
        
        print_navigation_element($prefix, $selected_page, "simulations/index.php",      "Simulations",
            get_sim_categories_html($prefix)
        );

        do_authentication(false);

        $login_option = '';
        $logout_option = '';
        
        if (!isset($GLOBALS['contributor_authenticated']) || $GLOBALS['contributor_authenticated'] == false) {
            $login_option = <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/login.php">→ Login</a></span></li>
EOT;
        }
        else {
            $logout_option = <<<EOT
                <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/user-logout.php">→ Logout</a></span></li>
EOT;
        }
        
        print_navigation_element($prefix, $selected_page, "teacher_ideas/index.php",   "Teacher Ideas &amp; Activities",
            <<<EOT
            <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/browse.php">→ Browse</a></span></li>
            
            $login_option

            <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/contribute.php">→ New Contribution</a></span></li>

            <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/manage-contributions.php">→ Manage Contributions</a></span></li>  
            
            <li class="sub"><span class="sub-nav"><a href="$prefix/teacher_ideas/user-edit-profile.php">→ Edit Profile</a></span></li>                
            
            $logout_option
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "get_phet/index.php",        "Download PhET",
            <<<EOT
            <li class="sub"><span class="sub-nav"><a href="$prefix/get_phet/full_install.php">→ Full Install</a></span></li>

            <li class="sub"><span class="sub-nav"><a href="$prefix/get_phet/simlauncher.php">→ Partial Install</a></span></li>
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "tech_support/index.php",    "Technical Support",
            <<<EOT
            <li class="sub"><span class="sub-nav"><a href="$prefix/tech_support/support-java.php">→ Java</a></span></li>

            <li class="sub"><span class="sub-nav"><a href="$prefix/tech_support/support-flash.php">→ Flash</a></span></li>
EOT
        );
        
        print_navigation_element($prefix, $selected_page, "contribute/index.php",      "Contribute",
            ''
        );
        
        print_navigation_element($prefix, $selected_page, "research/index.php",        "Research",
            ''
        );
        
        print_navigation_element($prefix, $selected_page, "about/index.php",           "About PhET",
            <<<EOT
            <li class="sub"><span class="sub-nav"><a href="$prefix/about/contact.php">→ Contact</a></span></li>

            <li class="sub"><span class="sub-nav"><a href="$prefix/about/licensing.php">→ Licensing</a></span></li>
EOT
        );
                    
        print <<<EOT
                </ul>

                <h4><br />
                Principle Sponsors</h4>

                <dl>
                    <dt><a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a></dt>

                    <dd><a href="http://www.hewlett.org/Default.htm">
                    <img src="$prefix/images/hewlett-logo.jpg" alt="The Hewlett Logo"/></a><br />
                    
                    <br />
                    Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                    <dt><a href="http://www.nsf.gov/">
                    <img class="sponsors" src="$prefix/images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a></dt>

                    <dd><br />
                    An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                    <br />
                    <a href="../sponsors/index.php">
                    <img src="$prefix/images/other-sponsors.gif" alt="Other Sponsors Logo"/></a></dd>
                </dl>
            </div>
EOT;
    }
    
    function print_site_page($content_printer, $selected_page = null) {
        global $referrer;
        
        $prefix = "..";
        
        print <<<EOT
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>PhET :: Physics Education Technology at CU Boulder</title>
                
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                
                <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
                
                <style type="text/css">
                /*<![CDATA[*/
                        @import url($prefix/css/main.css);
                /*]]>*/
                </style>    
                
                <script src="$prefix/js/jquery.pack.js"         type="text/javascript"></script>
                <script src="$prefix/js/jquery.MultiFile.js"    type="text/javascript"></script>
                <script src="$prefix/js/jquery.autocomplete.js" type="text/javascript"></script>
                <script src="$prefix/js/http.js"                type="text/javascript"></script>   
                
                <script type="text/javascript">
                    // AJAX login stuff:
                    /*<![CDATA[*/
                    
                    function on_email_entered() {
                        var name_element = document.getElementById('contributor_name_uid');
                        
                        var name = name_element.value;
                        
                        HTTP.updateElementWithGet('$prefix/admin/do-ajax-login.php?contributor_name=' + 
                            encodeURI(name), null, 'required_login_info_uid');
                    }

                    function on_remind_me() {
                        var email_element = document.getElementById('contributor_email_uid');

                        var email = email_element.value;
                        
                        var password_element = document.getElementById('ajax_password_comment_uid');

                        HTTP.updateElementWithGet('$prefix/admin/remind-password.php?contributor_email=' + 
                            encodeURI(email), null, 'ajax_password_comment_uid');
                    }
                        
                    function on_email_change() {
                        var email_element = document.getElementById('contributor_email_uid');

                        var email = email_element.value;

                        HTTP.updateElementWithGet('$prefix/admin/check-email.php?contributor_email=' + 
                            encodeURI(email), null, 'ajax_email_comment_uid');
                    }
                    
                    function on_password_change() {
                        var email_element    = document.getElementById('contributor_email_uid');
                        var password_element = document.getElementById('contributor_password_uid');
                        
                        var email    = email_element.value;
                        var password = password_element.value;

                        HTTP.updateElementWithGet('$prefix/admin/check-password.php?contributor_email=' + 
                            encodeURI(email) + '&contributor_password=' + 
                            encodeURI(password), null, 'ajax_password_comment_uid');
                    }
                    
                    $('#contributor_name_uid').autocomplete('$prefix/admin/get-contributor-names.php');
                    
                    /*]]>*/
                </script>             
            </head>
            

            <body>
                <div id="skipNav">
                    <a href="#content" accesskey="0">Skip to Main Content</a>
                </div>

                <div id="header">
                    <div id="headerContainer">
                        <div class="images">
                            <span class="logo">
                                <img src="$prefix/images/phet-logo.gif" alt="The PhET Logo" title="" />
                            </span>
                
                            <span class="title">
                                <img src="$prefix/images/logo-title.jpg" alt="The PhET Title" title="" />
                            </span>
                        </div>

                        <div class="clear"></div>

                        <div class="mainNav">
                            <ul>
EOT;

        print_header_navigation_element($prefix, $selected_page, "$prefix/index.php",              "Home",          1);
        print_header_navigation_element($prefix, $selected_page, "$prefix/simulations/index.php",  "Simulations",   2);
        print_header_navigation_element($prefix, $selected_page, "$prefix/research/index.php",     "Research",      7);
        print_header_navigation_element($prefix, $selected_page, "$prefix/about/index.php",        "About PhET",    8);
                                
       print <<<EOT
                            </ul>
                        </div>
                    </div>
                </div>
                
                <div id="quicksearch">
                    <form method="post" action="../simulations/search.php">
                        Search
                        <input type="text" size="15" name="search_for" />
                        <input type="submit" value="Go" />
                        <input type="hidden" name="referrer" value="$referrer" />
                    </form>
                </div>

                <div id="container">
EOT;

        print_navigation_bar($selected_page, $prefix);

        print <<<EOT
                    <div id="content">  
                        <div class="productList">
EOT;

        call_user_func($content_printer);

        print <<<EOT
                        </div>
                        
                        <div id="footer">
                            <p class="footer">© 2007 PhET. All rights reserved.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
EOT;
    }
    
 function print_blank_site_page($content_printer, $prefix = "..") {
        print <<<EOT
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>PhET :: Physics Education Technology at CU Boulder</title>

                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

                <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />

                <style type="text/css">
                /*<![CDATA[*/
                        @import url($prefix/css/main.css);
                /*]]>*/
                </style>    

                <script src="$prefix/js/jquery.pack.js"         type="text/javascript"></script>
                <script src="$prefix/js/jquery.MultiFile.js"    type="text/javascript"></script>
                <script src="$prefix/js/jquery.autocomplete.js" type="text/javascript"></script>
                <script src="$prefix/js/http.js"                type="text/javascript"></script>
                
                <script type="text/javascript">
                    /* <![CDATA[ */
                    
                        $('.showhide').click(
                            function() {
                                alert("This is an alert.");
                                
                                $(this).parent().next().toggle('slow');
                            
                                return false;
                            }
                        );
                    
                    /* ]]> */
                </script>
            </head>

            <body>
EOT;

        call_user_func($content_printer);

        print <<<EOT
            </body>
        </html>
EOT;
    }    