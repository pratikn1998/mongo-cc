.PHONY: venv install lint clean

VENV_DIR := .venv

venv:
	uv venv $(VENV_DIR)
	@echo "Virtual environment created at $(VENV_DIR)"
	@echo "To activate it: source $(VENV_DIR)/bin/activate"

install: venv
	@echo "Compiling dependencies from pyproject.toml..."
	uv pip compile pyproject.toml -o uv.lock
	. $(VENV_DIR)/bin/activate && uv pip install -r uv.lock
	@echo "Dependencies installed from uv.lock"


lint:
	$(VENV_DIR)/bin/ruff check .

clean:
	rm -rf $(VENV_DIR) __pycache__ .ruff_cache *.pyc *.pyo *.pyd *.log .pytest_cache .mypy_cache .coverage dist build


