<?php
/**
 * @param int $count
 * @return string
 */
function random(int $count) : string {
    $random = array_merge(range('a', 'z'), range('0', '9'), range('A', 'Z'));
    $string = null;
    for ($i = 0; $i < $count; $i++) {
        $string .= $random[rand(0, count($random) - 1)];
    }

    return $string;
}

/**
 * @param int $count
 * @return string
 */
function random_a0(int $count) : string {
    $random = array_merge(range('a', 'z'), range('0', '9'));
    $string = null;
    for ($i = 0; $i < $count; $i++) {
        $string .= $random[rand(0, count($random) - 1)];
    }

    return $string;
}

/**
 * @param int $count
 * @return string
 */
function random_0(int $count) : string {
    $random = array_merge(range('0', '9'));
    $string = null;
    for ($i = 0; $i < $count; $i++) {
        $string .= $random[rand(0, count($random) - 1)];
    }

    return $string;
}