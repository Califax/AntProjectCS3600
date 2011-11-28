@echo off
python "%~dp0playgame.py" --engine_seed 42 --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --turns 200 --fill --map_file "%~dp0maps\maze\maze_04p_01.map" %* "java -jar MyBot.jar" 

