@echo off
python "%~dp0playgame.py" --fill --engine_seed 42 --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --turns 100 --map_file "%~dp0maps\maze\maze_04p_01.map" dir C:\Users\Califax\workspace\AntsCS3600\src "java -jar MyBot.jar"
