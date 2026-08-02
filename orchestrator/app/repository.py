from contextlib import AbstractContextManager
from langgraph.checkpoint.postgres import PostgresSaver

class CheckpointRepository:
    """Owns the PostgreSQL-backed LangGraph checkpointer lifecycle."""
    def __init__(self, database_uri: str):
        self._context: AbstractContextManager = PostgresSaver.from_conn_string(database_uri)
        self.saver = None

    def open(self):
        self.saver = self._context.__enter__()
        self.saver.setup()
        return self.saver

    def close(self) -> None:
        self._context.__exit__(None, None, None)

