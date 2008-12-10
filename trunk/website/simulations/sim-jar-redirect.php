<?php

    // This file is called from sims.php to run a sim "offline" (download from Internet and run locally)

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    // Get the pertenant information
    if ((isset($_GET['project']) && (!empty($_GET['project']))) &&
        (isset($_GET['sim']) && (!empty($_GET['sim']))) &&
        (isset($_GET['language']) && (!empty($_GET['language'])))) {
        $dirname = $_GET['project'];
        $flavorname = $_GET['sim'];
        $language_code = $_GET['language'];
    }
    else {
        $error = "Error: Missing required information in the query string.\n";
        send_file_to_browser('error.txt', $error, null, "attachment");
        exit;
    }

    // Get the database info for the requested sim
    $simulation = sim_get_sim_by_dirname_flavorname($dirname, $flavorname);
    if (!$simulation) {
        $error = "Error: Simulation not found.\n";
        send_file_to_browser('error.txt', $error, null, "attachment");
        exit;
    }

    // Get the filename and content
    $download_data = sim_get_run_offline($simulation, $language_code);
    if (!$download_data) {
        $error = "Error: Simulation jar or language not found.\n";
        send_file_to_browser('error.txt', $error, null, "attachment");
        exit;
    }

    // Send the file as an attachment
    $filename = $download_data[0];
    $contents = $download_data[1];
    send_file_to_browser($filename, $contents, null, "attachment");

?>