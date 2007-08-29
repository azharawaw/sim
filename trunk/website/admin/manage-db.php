<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    
    function handle_action() {
        eval(get_code_to_create_variables_from_array($_REQUEST));
    
        if (isset($action)) {
            if ($action == "backup") {
                $GLOBALS['success'] = db_backup();

				print_site_page('print_backup', 9, "manage-db.php", 2);
            }
            else if ($action == "new") {
                $GLOBALS['success'] = db_restore();

				print_site_page('print_restore', 9, "manage-db.php", 2);
            }
        }
    }

	function print_backup() {
		global $success;
		
		if ($success) {
			print <<<EOT
				<p>The database was successfully backed up.</p>
EOT;
		}
		else {
			print <<<EOT
				<p>The database could not be backed up.</p>
EOT;
		}
	}
	
	function print_restore() {
		global $success;

		if ($success) {
			print <<<EOT
				<p>The database was successfully restored from the last backup.</p>
EOT;
		}
		else {
			print <<<EOT
				<p>The database could not be restored.</p>
EOT;
		}
	}	

    function print_db_management() {
		print <<<EOT
			<h1>Manage Database</h1>
			
			<p>From this page, you can manage the PhET database. <strong>You should not use these options unless you know what you are doing.</strong></p>
			
			<ul>
				<li><a href="manage-db.php?action=backup">Backup the database</a></li>
				
				<li><a href="manage-db.php?action=backup">Restore the database</a> from the last backup</li>
			</ul>
EOT;
    }
    
	if (isset($_REQUEST['action'])) {
    	handle_action();		
	}
	else {
    	print_site_page('print_db_management', 9);
	}

?>